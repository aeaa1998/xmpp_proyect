package Auth

import XMPP.XMPPConnectionsFactory
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smackx.iqregister.AccountManager
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
        }catch (e: Exception){
            println("Ocurrio un error inesperado al borrar la cuenta")
        }

        if (authConnection.isConnected){
            authConnection.connect()
        }

    }

    fun logout(notify: Boolean){
        loggedUser = null
        //TODO: Implement notification
    }

    fun createUser(username: String, password: String){
        val accountManager = AccountManager.getInstance(authConnection)
        accountManager.sensitiveOperationOverInsecureConnection(true)
        try {
            accountManager.createAccount(Localpart.from(username), password)
            println("Se ha creado el usuario con exito")
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
    }
}