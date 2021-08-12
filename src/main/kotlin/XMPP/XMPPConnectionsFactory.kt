package XMPP

import Auth.User
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration

object XMPPConnectionsFactory {
    fun create(user: User? = null): XMPPTCPConnection {
        val config = XMPPTCPConnectionConfiguration
            .builder()
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            .setServiceName("localhost")

//            .setResource("Rooster")
            .setHost("127.0.0.1")
//            .setKeystoreType(null)
//            .enableDefaultDebugger()
            .setPort(Constants.port)
            .apply {
                user?.let {
//                    setUsernameAndPassword(user.username, user.password)
                }
            }
//            .setDebuggerEnabled(true)
            .setSendPresence(true)
            .setCompressionEnabled(false)
//        XMPPTCPConnection.setUseStreamManagementResumptionDefault(true)
//        XMPPTCPConnection.setUseStreamManagementDefault(true)
        return XMPPTCPConnection(config.build())
    }
}