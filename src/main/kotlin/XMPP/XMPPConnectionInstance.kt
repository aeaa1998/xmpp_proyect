package XMPP

import Auth.Auth
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.roster.RosterListener
import org.jivesoftware.smack.roster.RosterLoadedListener
import org.jxmpp.jid.Jid
import java.lang.Exception

object XMPPConnectionInstance: RosterListener, RosterLoadedListener {
    val connection by lazy {
        XMPPConnectionsFactory.create().also {
            it.connect()
        }
    }
    val rooster: Roster?
        get() =
        if (Auth.logged && connection.isConnected){
            val r = Roster.getInstanceFor(connection)
            r.addRosterLoadedListener(this)
            r.addRosterListener(this)
            r
        }else{
            null
        }

    override fun entriesAdded(addresses: MutableCollection<Jid>?) {
//        print(addresses)
    }

    override fun entriesUpdated(addresses: MutableCollection<Jid>?) {
//        print(addresses)
    }

    override fun entriesDeleted(addresses: MutableCollection<Jid>?) {
//        print(addresses)
    }


    override fun presenceChanged(presence: Presence?) {

//        println(presence)
    }

    override fun onRosterLoaded(roster: Roster?) {
//        print(roster)
    }

    override fun onRosterLoadingFailed(exception: Exception?) {
        rooster?.reload()
//        exception?.printStackTrace()
    }


}