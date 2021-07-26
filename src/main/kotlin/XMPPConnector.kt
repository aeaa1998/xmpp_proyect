//import org.jivesoftware.smack.*
//import org.jivesoftware.smack.SmackException.NotConnectedException
//import org.jivesoftware.smack.chat2.Chat
//import org.jivesoftware.smack.chat2.ChatManager
//import org.jivesoftware.smack.tcp.XMPPTCPConnection
//import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
//import java.io.IOException
//
//
//class XMPPConnector {
//    private val DOMAIN = "nimbuzz.com"
//    private val HOST = "o.nimbuzz.com"
//    private val PORT = 5222
//    private var userName = ""
//    private var passWord = ""
//    var connection: XMPPTCPConnection? = null
//    var chatmanager: ChatManager? = null
//    var newChat: Chat? = null
//    var connectionListener = XMPPConnectionListener()
//    private var connected = false
//    private val isToasted = false
//    private val chat_created = false
//    private val loggedin = false
//
//
//    //Initialize
//    fun init(userId: String, pwd: String) {
//        print("XMPP: Initializing")
//        userName = userId
//        passWord = pwd
//
//        val configBuilder = XMPPTCPConnectionConfiguration.builder()
////        configBuilder.setUsernameAndPassword(userName, passWord)
//
//        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
//        configBuilder.setResource("Android")
//        configBuilder.setXmppDomain(DOMAIN)
//        configBuilder.setHost(HOST)
//        configBuilder.setPort(PORT)
//        //configBuilder.setDebuggerEnabled(true);
//        connection = XMPPTCPConnection(configBuilder.build())
//
//        connection?.addConnectionListener(connectionListener)
//    }
//
//    // Disconnect Function
//    fun disconnectConnection() {
//        Thread { connection!!.disconnect() }.start()
//    }
//
//    fun connectConnection() {
//        connection?.connect()
//        connected = true
//    }
//
//
//    fun sendMsg() {
//        if (connection?.isConnected == true) {
//            // Assume we've created an XMPPConnection name "connection"._
//            chatmanager = ChatManager.getInstanceFor(connection)
//            newChat = chatmanager?.createChat("concurer@nimbuzz.com")
//            try {
//                newChat.sendMessage("Howdy!")
//            } catch (e: NotConnectedException) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    fun login() {
//        try {
//            connection!!.login(userName, passWord)
//            //Log.i("LOGIN", "Yey! We're connected to the Xmpp server!");
//        } catch (e: XMPPException) {
//            e.printStackTrace()
//        } catch (e: SmackException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        } catch (e: Exception) {
//        }
//    }
//
//
//    //Connection Listener to check connection state
//    class XMPPConnectionListener : ConnectionListener {
//        override fun connected(connection: XMPPConnection) {
//            Log.d("xmpp", "Connected!")
//            connected = true
//            if (!connection.isAuthenticated) {
//                login()
//            }
//        }
//
//        override fun connectionClosed() {
//            if (isToasted) Handler(Looper.getMainLooper()).post(Runnable {
//                // TODO Auto-generated method stub
//            })
//            Log.d("xmpp", "ConnectionCLosed!")
//            connected = false
//            chat_created = false
//            loggedin = false
//        }
//
//        override fun connectionClosedOnError(arg0: Exception) {
//            if (isToasted) Handler(Looper.getMainLooper()).post(Runnable { })
//            Log.d("xmpp", "ConnectionClosedOn Error!")
//            connected = false
//            chat_created = false
//            loggedin = false
//        }
//
//        fun reconnectingIn(arg0: Int) {
//            Log.d("xmpp", "Reconnectingin $arg0")
//            loggedin = false
//        }
//
//        fun reconnectionFailed(arg0: Exception?) {
//            if (isToasted) Handler(Looper.getMainLooper()).post(Runnable { })
//            Log.d("xmpp", "ReconnectionFailed!")
//            connected = false
//            chat_created = false
//            loggedin = false
//        }
//
//        fun reconnectionSuccessful() {
//            if (isToasted) Handler(Looper.getMainLooper()).post(Runnable {
//                // TODO Auto-generated method stub
//            })
//            Log.d("xmpp", "ReconnectionSuccessful")
//            connected = true
//            chat_created = false
//            loggedin = false
//        }
//
//        override fun authenticated(arg0: XMPPConnection, arg1: Boolean) {
//            Log.d("xmpp", "Authenticated!")
//            loggedin = true
//            chat_created = false
//            Thread {
//                try {
//                    Thread.sleep(500)
//                } catch (e: InterruptedException) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace()
//                }
//            }.start()
//            if (isToasted) Handler(Looper.getMainLooper()).post(Runnable {
//                // TODO Auto-generated method stub
//            })
//        }
//    }
//}