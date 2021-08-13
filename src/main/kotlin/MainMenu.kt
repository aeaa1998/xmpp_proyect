import Auth.Auth
import Constants.GROUP_ROOSTER_NAME
import Utils.getIndexOfOption
import Utils.readString
import Utils.selectValidOption
import XMPP.XMPPConnectionInstance
import XMPP.XMPPConnectionInstance.connection
import XMPP.XMPPConnectionInstance.rooster
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.PresenceBuilder
import org.jivesoftware.smack.roster.*
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smackx.filetransfer.*
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jivesoftware.smackx.vcardtemp.packet.VCard
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.impl.LocalAndDomainpartJid
import org.jxmpp.jid.parts.Resourcepart
import java.io.File
import java.io.OutputStream


class MainMenu: IncomingChatMessageListener, MessageListener, FileTransferListener {

    val connection: XMPPTCPConnection
    get() = XMPPConnectionInstance.connection

    lateinit var chatManager: ChatManager
    lateinit var transferManager: FileTransferManager
    lateinit var groupChat: MultiUserChat
    var isInGroupChat = false
    var isInPersonalChat = false


    private fun mainMenuSecondary(){
        chatManager = ChatManager.getInstanceFor(connection)
        val multiUserChatManager = MultiUserChatManager.getInstanceFor(connection)
        multiUserChatManager.setAutoJoinOnReconnect(true)
        groupChat = multiUserChatManager.getMultiUserChat(JidCreate.entityBareFrom("everyone@conference.${connection.xmppServiceDomain}"))
        groupChat.join(Resourcepart.from(Auth.loggedUser?.username ?: ""))
        groupChat.addMessageListener(this)
        chatManager.addIncomingListener(this)
        transferManager = FileTransferManager.getInstanceFor(connection)
        transferManager.addFileTransferListener(this)


        groupChat.sendMessage("NOTIFICACION: Acaba de ingresar")
        val options = listOf(
            "Mostrar todos los usuarios",
            "Agregar un usuario a los contactos",
            "Mostrar detalles de un usuario",
            "Mensajes 1 a 1",
            "Chat group",
            "Enviar archivo dummy",
            "Definir mensaje de presencia",
            "Cerrar sesión"
//        "Enviar/recibir notificaciones",
        )
        while (true){
            when(options.getIndexOfOption()){
                0 -> {
                    val users = getUsers()
                    if (users.isEmpty()){
                        print("No hay mas usuarios en el servidor\n")
                    }else{
                        val contactUsers = getContactUsers()
                        println("----CONTACTOS----")
                        contactUsers.forEach { print(it.toString()) }
                        println()
                        val contactUsersIdentifiers = contactUsers.map { it.entry.jid.toString() }
                        println("----RESTO DE USUARIOS----")
                        users
                            .filter { !contactUsersIdentifiers.contains(it.entry.jid.toString()) }
                            .forEach { print(it.toString()) }
                    }
                }
                1 -> {
                    val contactUsers = getContactUsers()
                    val contactUsernames = contactUsers.map { it.entry.jid }
                    val users = getUsers().filter {
                        !contactUsernames.contains(it.entry.jid)
                    }
                    if (users.isEmpty()){
                        print("No hay usuarios que se puedan agregar\n")
                    }else{
                        val index = users.getIndexOfOption("Seleccione el numero de usuario que desea agregar al grupo de contactos")
                        var added = false
                        rooster?.getGroup(GROUP_ROOSTER_NAME)?.run {
                            addEntry(users[index].entry)
                            added = true
                        }
                        print(if (added) "Se agrego el usuario de manera correcta\n" else "Ocurrio un error al agergar al usuario pruebe de nuevo\n")
                    }
                }
                2 -> {
                    val users = getUsers()
                    val index = users.getIndexOfOption("Seleccione el numero de usuario que desea ver su info\n")
                    users[index].setVCard()
                    print(users[index].toString())
                    println()
                }
                3 -> {
                    val contactUsers = getContactUsers()
                    if (contactUsers.isEmpty()){
                        println("Para poder chatear 1-1 con alguien agregalos a tus usuarios")
                    }else{
                        val index = contactUsers.getIndexOfOption("Seleccione el numero de usuario con quien desea chatear")
                        try {
                            val chat = chatManager.chatWith(contactUsers[index].entry.jid.asEntityBareJidOrThrow())
                            isInPersonalChat = true
                            var input:String? = ""
                            while (input != "exit"){
                                input = readLine()
                                //Only non null messages will be shownlo qu
                                if (input != null){
                                    chat.send(input)
                                }
                            }
                            isInPersonalChat = false
                        }catch (e: Exception){
                            println("Ocurrio un error al iniciar el chat")
                        }

                    }
                }
                4 -> {
                    println("Ingrese exit para salir del grupo general")
                    var input:String? = ""
                    isInGroupChat = true
                    while (input != "exit"){
                        input = readLine()
                        //Only non null messages will be shownlo qu
                        if (input != null){
                            groupChat.sendMessage(input)
                        }
                    }
                    isInGroupChat = false

                }
                5 -> {
                    val contactUsers = getContactUsers()
                    if (contactUsers.isEmpty()){
                        println("Para poder enviar un arcivo hay que agregalos a tus usuarios")
                    }else{
                        val conected = contactUsers.filter { it.presence.isAvailable }
                        if (conected.isEmpty()){
                            print("No hay usuarios conectados\n")
                        }else{
                            val index = contactUsers.getIndexOfOption("Seleccione el numero de usuario con quien desea chatear")
                            try {
                                val bare = (contactUsers[index].entry.jid as LocalAndDomainpartJid).asEntityBareJid()
                                val fullString = "${contactUsers[index].entry.jid}/Lol"
                                rooster?.getPresence(contactUsers[index].entry.jid)?.from?.asEntityFullJidOrThrow()?.let {
                                    fullJid ->
                                    val full = JidCreate.entityFullFrom(fullString)
                                    val transfer = transferManager.createOutgoingFileTransfer(full)
                                    val file  = File("dummy.txt")
                                    if (file.exists()) {


                                        transfer.setCallback(object : OutgoingFileTransfer.NegotiationProgress {
                                            override fun statusUpdated(
                                                oldStatus: FileTransfer.Status?,
                                                newStatus: FileTransfer.Status?
                                            ) {
//                                                println(oldStatus)
                                            }

                                            override fun outputStreamEstablished(stream: OutputStream?) {
//                                                println(stream)
                                            }

                                            override fun errorEstablishingStream(e: java.lang.Exception?) {

                                            }

                                        })
                                        transfer.sendFile(file, "potente")
                                    }
                                }


                            }catch (e: Exception){
                                println("Ocurrio un error al enviar el archivo")
                            }
                        }
                    }

                }
                6 -> {
                    val index = listOf("Disponible", "No disponible").getIndexOfOption("Seleccione como quiere aparecer:\n")

                    val presenceName = readString("Ingrese su nueva presencia: \n", "El campo es obligatorio")
                    val presenceBuilder = PresenceBuilder
                        .buildPresence()


                    val presence = presenceBuilder
                        .setStatus(presenceName)
                        .ofType(Presence.Type.available)
                        .setMode(if (index == 0 ) Presence.Mode.available else Presence.Mode.dnd)
                        .build()


                    connection.sendStanza(presence)
                    println("Se cambio exitosamente el estado")
                }
                else ->{
                    Auth.loggedUser?.let {
                        groupChat.sendMessage("Notificación: ${it.username} ha cerrado sesión")
                    }
                    groupChat.leave()
                    Auth.logout()
                    break
                }
            }

        }
    }


    private fun getContactUsers(): List<User> {
        rooster?.let {
            roster ->
            val group = roster.getGroup(GROUP_ROOSTER_NAME)
            group?.let {
                    rosterGroup ->
                return rosterGroup.entries.map {
                    entry ->
                    val presence = roster.getPresence(entry.jid)
                    User(presence, entry, null)
                }
            }
            if (group == null){
                roster.createGroup(GROUP_ROOSTER_NAME)
                return getContactUsers()
            }
        }
        return emptyList()
    }


    private fun getUsers(): List<User> {
        rooster?.let {
            roster ->
            return roster.entries.map {
                entry ->

                val presence = roster.getPresence(entry.jid)
                User(presence, entry, null)
            }
        }
        return emptyList()
    }


    data class User(val presence: Presence, val entry: RosterEntry, var vCard: VCard?){
        override fun toString(): String {
            var vCardString = ""
            if (vCard != null){
                val name = "Nombre: ${vCard?.firstName} ${vCard?.lastName}\n"
                vCardString+=name
            }

            val username = "Usuario: ${entry.jid} \n"
            var presenceString = "Presencia: ${Constants.PRESCENCE_MAP[presence.type.name] ?: presence.type} \n"

            if(presence.type.ordinal == Presence.Type.available.ordinal && presence.mode != null && presence.mode.name.isNotBlank()){
                if (presence.mode.ordinal == Presence.Mode.dnd.ordinal){
                    presenceString += "Modo: No molestar\n"
                }else{
                    presenceString += "Modo: Listo para chatear\n"
                }
            }
            if(presence.status != null && !presence.status.isNullOrBlank()){
                presenceString += "Mensaje de presencia: ${presence.status}\n"
            }
            return vCardString + username + presenceString +"\n"
        }

        fun setVCard(){
            val vCard = VCardManager.getInstanceFor(connection)
            this.vCard = try {
                vCard.loadVCard(entry.jid.asEntityBareJidOrThrow())
            }catch (e: Exception){
                null
            }
        }
    }

    fun mainMenu(){
        val initialMenu = listOf("Registrar Cuenta", "Iniciar Sesión", "Eliminar cuenta", "Salir")
        var option: Int = initialMenu.getIndexOfOption()
        while (option != 3){
            when(option){
                0 -> {
                    //Registrar cuenta
                    val username = readString("Ingrese el usuario", "El usuario es un campo obligatorio\n")
                    val password = readString("Ingrese la contraseña", "La contraseña es un campo obligatorio\n")
                    val worked = Auth.createUser(username, password)
                    //If the user register log him in
                    if (worked){
                        mainMenuSecondary()
                    }
                }
                1 -> {
                    //Login
                    val username = readString("Ingrese el usuario", "El usuario es un campo obligatorio\n")
                    val password = readString("Ingrese la contraseña", "La contraseña es un campo obligatorio\n")
                    if (Auth.login(username = username, password, notify = true)){
                        mainMenuSecondary()
                    }
                }
                2 -> {
                    println("Para poder borrar su usuario le pediremos que ingrese sus credenciales")
                    val username = readString("Ingrese el usuario\n", "El usuario es un campo obligatorio\n")
                    val password = readString("Ingrese la contraseña\n", "La contraseña es un campo obligatorio\n")
                    if (Auth.login(username = username, password, notify = false)){
                        //We are logged
                        Auth.deleteAccount()
                    }
                }
                3 -> {
                    XMPPConnectionInstance.connection.disconnect()
                }
            }
            option = initialMenu.getIndexOfOption()
        }
        print("Gracias por usar el programa")
    }

    //PERSONAL
    override fun newIncomingMessage(from: EntityBareJid?, message: Message?, chat: Chat?) {
        val fromR = from?.localpart.toString()
        if (isInPersonalChat){
            println("$fromR: ${message?.body}")
        }else{
            println("Nuevo mensaje de $fromR")
        }

    }

    //GROUP
    override fun processMessage(message: Message?) {
        if (isInGroupChat){
            message?.let {
                message ->
//                val from = (message.from as? LocalDomainAndResourcepartJid)?.resourcepart.toString() ?: message.from.toString()
                val index = message.from.indexOf("/")
                val fromUsername = message.from.substring(index+1)
                if (fromUsername != Auth.loggedUser?.username){
                    println("$fromUsername: ${message.body}")
                }
            }
        }
    }

    override fun fileTransferRequest(request: FileTransferRequest?) {
        request?.let {
            r ->
            val transfer = request.accept()

            try {
                val username = Auth.loggedUser?.username.toString()
                val file = File("${username}-${request.fileName}")
                val created = true
                if (created){
                    transfer.receiveFile(file)
                }else{
                    transfer.cancel()
                }
                println("Recibiendo archivo de ${request.requestor}")
                while (!transfer.isDone){ }
                println("Se ha completado de recibir el archivo ${file.name}")
            }catch (e: Exception){
                print("Error al recibir archivo\n")
                transfer.cancel()
            }
        }

    }

}