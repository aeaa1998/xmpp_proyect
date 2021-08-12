package Auth

import Utils.readString
import XMPP.XMPPConnectionInstance.connection
import XMPP.XMPPConnectionsFactory
import com.sun.org.apache.xpath.internal.operations.Bool
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.PresenceBuilder
import org.jivesoftware.smackx.iqregister.AccountManager
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.parts.Localpart
import java.io.IOException


object Auth {
    val authConnection by lazy {
        XMPPConnectionsFactory.create().also {
            it.connect()
        }
    }




    var loggedUser: User? = null
    set(value) {
        authConnection.disconnect()
        field = value
    }

    val logged: Boolean
    get() = loggedUser != null




    fun login(username: String, password: String, notify: Boolean): Boolean{
        try {
            if (!connection.isConnected){
                connection.connect()
            }
            authConnection.login(username, password)
            loggedUser = User(username, password)
            println("Se ha iniciado con exito la sesión")
            return true
        }catch (e: XMPPException){
            println("Credenciales incorrectas")
        }catch (e: SmackException){
            println("Error inesperado")
        }catch (e: IOException){
            println("Hubo un error al procesar su informacion pruebe de nuevo")
        }catch (e: InterruptedException){
            println("Se ha interrumpido la llamada por facor probar de nuevo")
        }
        return false
    }

    fun deleteAccount(){
        val accountManager = AccountManager.getInstance(authConnection)
        accountManager.sensitiveOperationOverInsecureConnection(true)
        try {
            accountManager.deleteAccount()
            println("Se elimino de manera exitosa el usaurio")
        }catch (e: Exception){
            println("Ocurrio un error inesperado al borrar la cuenta")
        }

        if (!authConnection.isConnected){
            authConnection.connect()
        }

    }

    fun logout(){
        loggedUser = null
        connection.connect()
    }

    fun createUser(username: String, password: String) : Boolean {
        val accountManager = AccountManager.getInstance(authConnection)
        accountManager.sensitiveOperationOverInsecureConnection(true)
        val firstName = readString("Ingrese su primer nombre")
        val lastName = readString("Ingrese su primer apellido")
        val email = readString("Ingrese su email")
        try {
            if (!connection.isConnected){
                connection.connect()
            }
            accountManager.createAccount(Localpart.from(username), password)
            val vCard = VCardManager.getInstanceFor(connection)
            val card = vCard.loadVCard()
            card.firstName = firstName
            card.nickName = "$firstName $lastName"
            card.emailHome = email
            card.emailWork = email
            card.lastName = lastName
            connection.sendStanza(PresenceBuilder.buildPresence().ofType(Presence.Type.subscribe).build())
            vCard.saveVCard(card)
            loggedUser = User(username, password)
            println("Se ha creado el usuario con exito")
            return true
        } catch (e: SmackException.NoResponseException){
            println("No se obtuvo respuesta del servidor")
            e.printStackTrace()
        }catch (e: SmackException.NotConnectedException){
            println("La coneccion aun no se ha iniciado")
            e.printStackTrace()
        }catch (e: InterruptedException){
            println("Se ha interrumpido la llamada por facor probar de nuevo")
        }catch (e: XMPPException.XMPPErrorException){
//            if (e.stanzaError)
            if (e.stanzaError.descriptiveText.toLowerCase() == "el usuario ya existe"){
                println("El usuario: $username ya existe")
            }
        }
        return false
    }
}