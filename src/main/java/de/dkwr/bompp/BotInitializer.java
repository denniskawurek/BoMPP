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

import de.dkwr.bompp.cmd.exec.CommandQueue;
import de.dkwr.bompp.cmd.handler.CommandHandler;
import de.dkwr.bompp.cmd.handler.ScriptCommandHandler;
import de.dkwr.bompp.omemo.BotTrustCallback;
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
import org.jivesoftware.smackx.omemo.OmemoStore;
import org.jivesoftware.smackx.omemo.listener.OmemoMessageListener;
import org.jivesoftware.smackx.omemo.listener.OmemoMucMessageListener;
import org.jivesoftware.smackx.omemo.signal.SignalCachingOmemoStore;
import org.jivesoftware.smackx.omemo.signal.SignalFileBasedOmemoStore;
import org.jivesoftware.smackx.omemo.signal.SignalOmemoService;

/**
 * The BotInitializer creates an XMPP Connection and initializes all objects to listen for Omemo Messages.
 * @author Dennis Kawurek
 */
public class BotInitializer {
    private AbstractXMPPConnection connection;
    private OmemoManager omemoManager;
    private OmemoStore omemoStore;
    private OmemoMessageListener omemoMessageListener;
    private OmemoMucMessageListener omemoMucMessageListener;
    private OmemoController omemoController;
    private ChatManager chatManager;
    private Roster roster;
    private CommandHandler scriptCommandHandler;

    /**
     * Initializes the XMPP connection and OMEMOManager for the bot with all
     * message listeners.
     * @param jid the XMPP/Jabber Id of the bot
     * @param pwd the corresponding password
     * @param path the store path
     * @param commandQueue the CommandQueue where the commands belong to
     * @param enableXMPPDebugMode true|false whether the XMPP debug mode provided by smack shall be enabled
     */
    public void init(String jid, char[] pwd, String path, CommandQueue commandQueue, boolean enableXMPPDebugMode) {
        try {
            System.out.println("Start initializing BoMPP please wait...");
            SmackConfiguration.DEBUG = enableXMPPDebugMode;
            OmemoConfiguration.setAddOmemoHintBody(false);

            this.connection = new XMPPTCPConnection(jid, new String(pwd));
            SignalOmemoService.acknowledgeLicense();
            SignalOmemoService.setup();
            //OmemoConfiguration.setFileBasedOmemoStoreDefaultPath(new File(path));
            SignalOmemoService service = (SignalOmemoService) SignalOmemoService.getInstance();
            service.setOmemoStoreBackend(new SignalCachingOmemoStore(new SignalFileBasedOmemoStore(new File(path))));
            this.omemoManager = OmemoManager.getInstanceFor(connection);
            this.omemoManager.setTrustCallback(BotTrustCallback.getInstance());
            this.omemoStore = OmemoService.getInstance().getOmemoStoreBackend();
            this.connection.setReplyTimeout(10000);
            this.connection = connection.connect();
            this.connection.login();
            this.omemoManager.initialize();

            CarbonManager.getInstanceFor(connection).enableCarbons();

            this.roster = Roster.getInstanceFor(connection);
            this.roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
            
            this.scriptCommandHandler = new ScriptCommandHandler(commandQueue);
            MessageListener messageListener = new MessageListener(scriptCommandHandler);
            this.omemoManager.addOmemoMessageListener(messageListener.setupOmemoMessageListener());
            this.omemoManager.addOmemoMucMessageListener(messageListener.setupOmemoMucMessageListener());
            
            this.chatManager = ChatManager.getInstanceFor(connection);
            this.chatManager.addIncomingListener(messageListener.setupIncomingMessageListener());
            
            this.omemoController = new OmemoController(this.connection, this.omemoManager, this.omemoStore, this.roster, this.chatManager);
            
            this.scriptCommandHandler.setOmemoController(this.omemoController);
            
            System.out.println("OMEMO setup complete and BoMPP started!");
        } catch (Exception ex) {
            BotLogger.getInstance().logException(ex);
            System.out.println("Connection failed. Is your password ok?\nQuit.");
            System.exit(-1);
        }
    }
    
    /**
     * Returns the OmemoController for the connection.
     * @return an OmemoController
     */
    public OmemoController getOmemoController() {
        return this.omemoController;
    }

    public CommandHandler getScriptCommandHandler() {
        return this.scriptCommandHandler;
    }
}
