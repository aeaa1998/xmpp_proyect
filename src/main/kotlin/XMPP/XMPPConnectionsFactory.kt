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
            .setResource("Lol")
            .setXmppDomain("localhost")
            .setHost("127.0.0.1")
            .setPort(Constants.port)
            .setSendPresence(true)
            .setCompressionEnabled(false)
        return XMPPTCPConnection(config.build())
    }
}