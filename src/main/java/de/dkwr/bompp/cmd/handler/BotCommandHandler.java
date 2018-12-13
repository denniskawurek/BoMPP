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
package de.dkwr.bompp.cmd.handler;

import de.dkwr.bompp.cmd.exec.CommandQueue;
import de.dkwr.bompp.util.BotConfiguration;
import de.dkwr.bompp.util.BotLogger;
import de.dkwr.bompp.xmpp.OmemoController;
import de.dkwr.bompp.util.CommandList;
import de.dkwr.bompp.util.ConfigFileWatcher;
import de.dkwr.bompp.util.ConfigReader;
import de.dkwr.bompp.util.StaticScanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jxmpp.jid.BareJid;

/**
 * The BotCommandHandler handles all operations which are only available for the
 * administrator of the bot. This operations are mainly in the
 * {@link OmemoController}
 *
 * @author Dennis Kawurek
 */
public class BotCommandHandler implements CommandHandler {

    private OmemoController omemoController;
    private final ConfigReader configReader;
    private final CommandQueue commandQueue;
    private final CommandHandler scriptCommandHandler;
    private final BareJid adminJID;
    private final BotConfiguration cfg = BotConfiguration.getInstance();

    public BotCommandHandler(OmemoController omemoController, ConfigReader configReader, CommandQueue commandQueue, CommandHandler scriptCommandHandler, BareJid adminJID) {
        this.omemoController = omemoController;
        this.configReader = configReader;
        this.commandQueue = commandQueue;
        this.scriptCommandHandler = scriptCommandHandler;
        this.adminJID = adminJID;
    }

    @Override
    public void handleCommand(String cmd) {
        String[] cmdArr = cmd.split(" ");

        if (cmdArr[0].equalsIgnoreCase("/help")) {
            System.out.println(this.getAllCommandsAsString());
            return;
        }

        if (cmdArr[0].equalsIgnoreCase("/send")) {
            if (cmdArr.length < 3) {
                System.out.println("Error: To send a message you have to call\n"
                        + "/send [JID] [MESSAGE]");
                return;
            }
            try {
                String message = cmd.substring(cmd.indexOf(' ')).trim();
                message = message.substring(message.indexOf(' ')).trim();
                this.omemoController.sendMessage(this.omemoController.getJid(cmdArr[1]), message);
            } catch (Exception ex) {
                System.out.println("Failed to send message.");
                BotLogger.getInstance().logException(ex);
            }
            return;
        }

        if (cmdArr[0].equalsIgnoreCase("/chat") && cmdArr.length == 2) {
            if (cmdArr.length < 2) {
                System.out.println("Error: To open a chat call\n"
                        + "/chat [JID]");
                return;
            }

            this.cfg.openChat(this.omemoController.getJid(cmdArr[1]));
            System.out.println("Chat opened.");
            return;
        }

        if (cmdArr[0].equalsIgnoreCase("/close")) {
            if (this.cfg.isChatOpened()) {
                this.cfg.closeChat();
                System.out.println("Chat closed.");
                return;
            }
            System.out.println("No chat opened.");
            return;
        }

        if (cmdArr[0].equalsIgnoreCase("/list")) {
            try {
                if (cmdArr.length == 1) {
                    this.omemoController.listAll(null);
                } else if (cmdArr.length == 2) {
                    this.omemoController.listAll(cmdArr[0]);
                }
            } catch (Exception ex) {
                BotLogger.getInstance().logException(ex);
            }
            return;
        }

        if (cmdArr[0].equalsIgnoreCase("/trust")) {
            if (cmdArr.length != 2) {
                return;
            }
            this.omemoController.trustIdentities(cmdArr[1]);
            return;
        }

        if (cmdArr[0].equalsIgnoreCase("/clear")) {
            this.omemoController.clearDeviceList();
            return;
        }

        if (cmdArr[0].equalsIgnoreCase("/regenerate")) {
            try {
                this.omemoController.regenerateKeys();
            } catch (Exception ex) {
                BotLogger.getInstance().logException(ex);
            }
            return;
        }

        if (cmdArr[0].equalsIgnoreCase("/fingerprint")) {
            try {
                System.out.println(
                        this.omemoController.getFingerprint().toString()
                );
                return;
            } catch (Exception ex) {
                BotLogger.getInstance().logException(ex);
            }
        }

        if (cmdArr[0].equalsIgnoreCase("/which")) {
            try {
                this.omemoController.printSelfJID();
                System.out.println(
                        "Your fingerprint: "
                        + this.omemoController.getFingerprint().toString()
                );
                return;
            } catch (Exception ex) {
                BotLogger.getInstance().logException(ex);
            }
        }

        if (cmdArr[0].equalsIgnoreCase("/reload")) {
            try {
                this.configReader.reloadConfigFile();
            } catch (Exception ex) {
                BotLogger.getInstance().logException(ex);
            }
            return;
        }

        if (cmdArr[0].equalsIgnoreCase("/commands")) {
            try {
                System.out.println(CommandList.getInstance().toString());
            } catch (Exception ex) {
                BotLogger.getInstance().logException(ex);
            }
            return;
        }

        if (cmdArr[0].equalsIgnoreCase("/exec")) {
            String execCmd = cmd.substring(cmdArr[0].length() + 1);
            System.out.println(execCmd);
            this.scriptCommandHandler.handleCommand(execCmd);
            return;
        }

        if (cmdArr[0].equalsIgnoreCase("/q")) {
            try {
                this.omemoController.closeConnection();
                StaticScanner.close();
                this.commandQueue.quitCommandExecution();
                ConfigFileWatcher.getInstance().stopWatching();
            } catch (Exception ex) {
                BotLogger.getInstance().logException(ex);
            }
            return;
        }

        if (this.cfg.isChatOpened()) {
            try {
                System.out.println("Sending message to " + this.cfg.getOpenedChat().toString());
                this.omemoController.sendMessage(this.cfg.getOpenedChat(), cmd);
            } catch (Exception ex) {
                BotLogger.getInstance().logException(ex);
            }
            return;
        }

        // if command does not exist AND no chat is opened print available commands
        if (!this.cfg.isChatOpened()) {
            System.out.println(this.getAllCommandsAsString());
        }
    }

    @Override
    public String getAllCommandsAsString() {
        StringBuilder cmdStr = new StringBuilder();
        String listFmt = "%-25s%-25s\n";

        cmdStr.append("Following commands are available:\n");
        cmdStr.append(String.format(listFmt, "/send [JID] [MESSAGE]", "Send a message to [JID]"));
        cmdStr.append(String.format(listFmt, "/chat [JID]", "Open a chat with [JID] so you don't have to call the /send command."));
        cmdStr.append(String.format(listFmt, "/close", "Close the chat"));
        cmdStr.append(String.format(listFmt, "/list", "List all devices"));
        cmdStr.append(String.format(listFmt, "/list [JID]", "List all devices for [JID]"));
        cmdStr.append(String.format(listFmt, "/trust [JID]", "Trust the identity of [JID]"));
        cmdStr.append(String.format(listFmt, "/clear", "Clear the device list"));
        cmdStr.append(String.format(listFmt, "/regenerate", "Regenerate keys"));
        cmdStr.append(String.format(listFmt, "/fingerprint", "Print the bots fingerprint"));
        cmdStr.append(String.format(listFmt, "/which", "Print bots JID and DeviceId"));
        cmdStr.append(String.format(listFmt, "/reload", "Reloads the configuration file"));
        cmdStr.append(String.format(listFmt, "/commands", "Prints loaded commands"));
        cmdStr.append(String.format(listFmt, "/exec CMD", "Execute a command from Bot CLI"));
        cmdStr.append(String.format(listFmt, "/q", "Closes the connection & ends the bot"));

        return cmdStr.toString();
    }

    @Override
    public void handleCommand(String cmd, String jid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setOmemoController(OmemoController omemoController) {
        this.omemoController = omemoController;
    }

    public void sendAdminMessage(String message) {
        try {
            this.omemoController.sendMessage(this.adminJID, message);
        } catch (Exception ex) {
            System.out.println("Failed to send message to Admin.");
            BotLogger.getInstance().logException(ex);
        }
    }
}
