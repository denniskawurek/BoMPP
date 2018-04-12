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
package de.dkwr.bompp;

import de.dkwr.bompp.commandexecutor.CommandQueue;
import de.dkwr.bompp.commandhandler.CommandHandler;
import de.dkwr.bompp.commandhandler.ScriptCommandHandler;
import de.dkwr.bompp.util.BotLogger;
import de.dkwr.bompp.omemo.MessageListener;
import de.dkwr.bompp.omemo.OmemoController;

import java.io.File;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.jivesoftware.smackx.omemo.OmemoConfiguration;
import org.jivesoftware.smackx.omemo.OmemoManager;
import org.jivesoftware.smackx.omemo.OmemoService;
import org.jivesoftware.smackx.omemo.listener.OmemoMessageListener;
import org.jivesoftware.smackx.omemo.listener.OmemoMucMessageListener;
import org.jivesoftware.smackx.omemo.signal.SignalFileBasedOmemoStore;
import org.jivesoftware.smackx.omemo.signal.SignalOmemoService;

/**
 * The BotInitializer creates an XMPP Connection and initializes all objects to listen for Omemo Messages.
 * @author Dennis Kawurek
 */
public class BotInitializer {
    private AbstractXMPPConnection connection;
    private OmemoManager omemoManager;
    private SignalFileBasedOmemoStore omemoStore;
    private OmemoMessageListener omemoMessageListener;
    private OmemoMucMessageListener omemoMucMessageListener;
    private OmemoController omemoController;
    private ChatManager chatManager;
    private Roster roster;

    /**
     * Initializes the XMPP connection and OMEMOManager for the bot with all
     * message listeners.
     * @param jid the XMPP/Jabber Id of the bot
     * @param pwd the corresponding password
     * @param path the store path
     * @param commandQueue the CommandQueue where the commands belong to
     * @param enableXMPPDebugMode true|false whether the XMPP debug mode provided by smack shall be enabled
     */
    public void init(String jid, String pwd, String path, CommandQueue commandQueue, boolean enableXMPPDebugMode) {
        try {
            SmackConfiguration.DEBUG = enableXMPPDebugMode;
            OmemoConfiguration.setAddOmemoHintBody(false);

            this.connection = new XMPPTCPConnection(jid, pwd);

            SignalOmemoService.acknowledgeLicense();
            SignalOmemoService.setup();
            OmemoConfiguration.setFileBasedOmemoStoreDefaultPath(new File(path));
            
            this.omemoManager = OmemoManager.getInstanceFor(connection);
            this.omemoStore = (SignalFileBasedOmemoStore) OmemoService.getInstance().getOmemoStoreBackend();
            this.connection.setPacketReplyTimeout(10000);
            this.connection = connection.connect();
            this.connection.login();

            CarbonManager.getInstanceFor(connection).enableCarbons();

            this.roster = Roster.getInstanceFor(connection);
            this.roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
            
            CommandHandler scriptCommandHandler = new ScriptCommandHandler(commandQueue);
            MessageListener messageListener = new MessageListener(scriptCommandHandler);
            this.omemoManager.addOmemoMessageListener(messageListener.setupOmemoMessageListener());
            this.omemoManager.addOmemoMucMessageListener(messageListener.setupOmemoMucMessageListener());
            
            this.chatManager = ChatManager.getInstanceFor(connection);
            this.chatManager.addIncomingListener(messageListener.setupIncomingMessageListener());
            
            this.omemoController = new OmemoController(this.connection, this.omemoManager, this.omemoStore, this.roster, this.chatManager);
            
            scriptCommandHandler.setOmemoController(this.omemoController);
            
            System.out.println("OMEMO setup complete. You can now start chatting.");
        } catch (Exception ex) {
            BotLogger.getInstance().logException(ex);
        }
    }
    
    /**
     * Returns the OmemoController for the connection.
     * @return an OmemoController
     */
    public OmemoController getOmemoController() {
        return this.omemoController;
    }
}
