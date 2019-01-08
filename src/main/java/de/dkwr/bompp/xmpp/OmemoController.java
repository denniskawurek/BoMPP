/*
 * Copyright (C) 2018 Dennis Kawurek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dkwr.bompp.xmpp;

import de.dkwr.bompp.util.BotLogger;
import de.dkwr.bompp.util.StaticScanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.omemo.OmemoManager;
import org.jivesoftware.smackx.omemo.OmemoMessage;
import org.jivesoftware.smackx.omemo.OmemoStore;
import org.jivesoftware.smackx.omemo.internal.OmemoCachedDeviceList;
import org.jivesoftware.smackx.omemo.internal.OmemoDevice;
import org.jivesoftware.smackx.omemo.trust.OmemoFingerprint;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.whispersystems.libsignal.IdentityKey;

/**
 * The OmemoController provides methods to control Omemo specicific settings.
 *
 * @author Dennis Kawurek based on @see
 * <a href="https://github.com/vanitasvitae/clocc/">CLOCC by vanitasvitae</a>
 */
public class OmemoController {
    
    private final OmemoManager omemoManager;
    private final OmemoStore omemoStore;
    private final AbstractXMPPConnection connection;
    private final ChatManager chatManager;
    private final Roster roster;

    /**
     *
     * @param connection The XMPP Connection of the Bot
     * @param omemoManager The OmemoManager associated with the session
     * @param omemoStore The Storage of the bot
     * @param roster The Roster associated with the session
     * @param chatManager The ChatManager used to send messages
     */
    public OmemoController(AbstractXMPPConnection connection, OmemoManager omemoManager, OmemoStore omemoStore, Roster roster, ChatManager chatManager) {
        this.omemoManager = omemoManager;
        this.omemoStore = omemoStore;
        this.connection = connection;
        this.chatManager = chatManager;
        this.roster = roster;
    }

    /**
     * Sends a message to a JID
     *
     * @param recipient the receiver
     * @param message the message
     * @throws Exception when there is no connection or the encryption fails
     */
    public void sendMessage(BareJid recipient, String message) throws Exception {
        OmemoMessage.Sent encrypted = this.omemoManager.encrypt(recipient, message);
        Message msg = encrypted.asMessage(recipient);
        this.connection.sendStanza(msg);
    }

    /**
     * Lists the whole device list or a list for a JID.
     *
     * @param jidStr if is String: Lists all entries for the JID, if is NULL:
     * Lists all entries
     * @throws Exception when the device list request fails.
     */
    public void listAll(String jidStr) throws Exception {
        if (jidStr == null) {
            for (RosterEntry r : roster.getEntries()) {
                System.out.println(r.getName() + " (" + r.getJid() + ") Can I see? "
                        + r.canSeeHisPresence() + ". Can they see? " + r.canSeeMyPresence()
                        + ". Online? " + roster.getPresence(r.getJid()).isAvailable());
            }
        } else {
            BareJid jid = getJid(jidStr);
            try {
                List<Presence> presences = roster.getAllPresences(jid);
                for (Presence p : presences) {
                    System.out.println(p.getFrom() + " "
                            + omemoManager
                                    .contactSupportsOmemo(
                                    p.getFrom()
                                            .asEntityBareJidIfPossible()
                                            .asBareJid()
                                    ));
                }
            } catch (Exception e) {
                BotLogger.getInstance().logException(e);
            }
            
            try {
                this.omemoManager.requestDeviceListUpdateFor(jid);
                Set<OmemoDevice> devices = this.omemoManager.getDevicesOf(jid);
                for (OmemoDevice contactDevice : devices) {
                    OmemoCachedDeviceList list
                            = omemoStore.loadCachedDeviceList(contactDevice);
                    
                    if (list == null) {
                        list = new OmemoCachedDeviceList();
                    }
                    
                    ArrayList<String> fps = new ArrayList<>();
                    
                    for (int i : list.getActiveDevices()) {
                        OmemoDevice d = new OmemoDevice(jid, i);
                        IdentityKey idk = (IdentityKey) this.omemoStore.loadOmemoIdentityKey(this.omemoManager.getOwnDevice(), d);
                        if (idk == null) {
                            System.out.println("No identityKey for " + d);
                        } else {
                            fps.add(idk.getFingerprint());
                        }
                    }
                    for (int i = 0; i < fps.size(); i++) {
                        System.out.println(i + ":" + fps.get(i));
                    }
                }
            } catch (Exception e) {
                BotLogger.getInstance().logException(e);
            }
        }
    }

    /**
     * Trusts an identity.
     *
     * @param jidStr the JID to trust.
     */
    public void trustIdentities(String jidStr) {
        System.out.println("Usage: \n0: Untrusted, 1: Trusted, otherwise: Undecided");
        BareJid jid = getJid(jidStr);
        
        if (jid == null) {
            return;
        }
        
        Set<OmemoDevice> devices = this.omemoManager.getDevicesOf(jid);
        for (OmemoDevice dvc : devices) {
            try {
                OmemoFingerprint fingerprint = this.omemoManager.getFingerprint(dvc);
                if (this.omemoManager.isDecidedOmemoIdentity(dvc, this.omemoManager.getFingerprint(dvc))) {
                    if (this.omemoManager.isTrustedOmemoIdentity(dvc, this.omemoManager.getFingerprint(dvc))) {
                        System.out.println("Status: Trusted");
                    } else {
                        System.out.println("Status: Untrusted");
                    }
                } else {
                    System.out.println("Status: Undecided");
                }
                System.out.println(this.omemoManager.getFingerprint(dvc));
                System.out.println("Press 0 to untrust or 1 to trust.");
                String decision = StaticScanner.scanner.nextLine();
                if (decision.equals("0")) {
                    this.omemoManager.distrustOmemoIdentity(dvc, this.omemoManager.getFingerprint(dvc));
                    System.out.println("Identity has been untrusted.");
                } else if (decision.equals("1")) {
                    this.omemoManager.trustOmemoIdentity(dvc, this.omemoManager.getFingerprint(dvc));
                    System.out.println("Identity has been trusted.");
                }
            } catch (Exception ex) {
                BotLogger.getInstance().logException(ex);
            }
        }
    }

    /**
     * Clears the device list.
     */
    public void clearDeviceList() {
        try {
            omemoManager.purgeDeviceList();
        } catch (Exception ex) {
            BotLogger.getInstance().logException(ex);
        }
    }

    /**
     * Creates new keys for the bot
     *
     * @throws Exception when creating keys fails.
     */
    public void regenerateKeys() throws Exception {
        //this.omemoManager.regenerate();
    }

    /**
     * Returns the fingerprint of the bot.
     *
     * @return the fingerprint
     */
    public OmemoFingerprint getFingerprint() throws Exception {
        return this.omemoManager.getOwnFingerprint();
    }

    /**
     * Prints the JID and device Id of the bot.
     */
    public void printSelfJID() {
        String ownDeviceId = this.omemoManager.getDeviceId().toString();
        String ownJid = this.omemoManager.getOwnJid().toString();
        System.out.println(
                "JID: " + ownJid + "\n"
                + "DeviceId: " + ownDeviceId
        );
    }

    /**
     * Closes the connection of the bot.
     *
     * @throws Exception when closing fails.
     */
    public void closeConnection() throws Exception {
        this.connection.disconnect(new Presence(Presence.Type.unavailable, "You are still connected.", 100, Presence.Mode.away));
    }
    
    public Roster getRoster() {
        return this.roster;
    }
    
    public BareJid getJid(String user) {
        RosterEntry r = null;
        for (RosterEntry s : this.roster.getEntries()) {
            if (s.getName() != null && s.getName().equals(user)) {
                r = s;
                break;
            }
        }
        if (r != null) {
            return r.getJid();
        } else {
            try {
                return JidCreate.bareFrom(user);
            } catch (XmppStringprepException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
