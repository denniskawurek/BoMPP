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
package de.dkwr.bompp.commandhandler;

import de.dkwr.bompp.commandexecutor.CommandQueue;
import de.dkwr.bompp.util.BotLogger;
import de.dkwr.bompp.omemo.OmemoController;
import de.dkwr.bompp.util.ConfigReader;
import de.dkwr.bompp.util.StaticScanner;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smackx.omemo.util.OmemoKeyUtil;

/**
 * The BotCommandHandler handles all operations which are only available for the
 * administrator of the bot. This operations are mainly in the
 * {@link OmemoController}
 *
 * @author Dennis Kawurek
 */
public class BotCommandHandler extends CommandHandler {

    private OmemoController omemoController;
    private final ConfigReader configReader;
    private final CommandQueue commandQueue;

    public BotCommandHandler(OmemoController omemoController, ConfigReader configReader, CommandQueue commandQueue) {
        this.omemoController = omemoController;
        this.configReader = configReader;
        this.commandQueue = commandQueue;
    }

    @Override
    public void handleCommand(String cmd) {
        String[] cmdArr = cmd.split(" ");

        if (cmdArr[0].equalsIgnoreCase("/help")) {
            System.out.println(this.getAllCommandsAsString());
            return;
        }

        if (cmdArr[0].equalsIgnoreCase("/send")) {
            if (cmdArr.length != 3) {
                System.out.println("Error: To send a message you have to call\n"
                        + "/send [JID] [MESSAGE]");
                return;
            }
            try {
                this.omemoController.sendMessage(cmdArr[1], cmdArr[2]);
            } catch (Exception ex) {
                System.out.println("Failed to send message.");
                BotLogger.logException(ex);
            }
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
                BotLogger.logException(ex);
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
                BotLogger.logException(ex);
            }
            return;
        }

        if (cmdArr[0].equalsIgnoreCase("/fingerprint")) {
            System.out.println(
                    OmemoKeyUtil.prettyFingerprint(this.omemoController.getFingerprint())
            );
            return;
        }

        if (cmdArr[0].equalsIgnoreCase("/which")) {
            this.omemoController.printSelfJID();
            return;
        }
        
        if (cmdArr[0].equalsIgnoreCase("/reload")) {
            try {
                this.configReader.reloadConfigFile();
            } catch (Exception ex) {
                BotLogger.logException(ex);
            }
        }

        if (cmdArr[0].equalsIgnoreCase("/q")) {
            try {
                this.omemoController.closeConnection();
                StaticScanner.scanner.close();
                this.commandQueue.quitCommandExecution();
            } catch (Exception ex) {
                Logger.getLogger(BotCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        
        // if command not exists print available commands
        System.out.println(this.getAllCommandsAsString());
    }

    @Override
    public String getAllCommandsAsString() {
        return "Following commands are available:\n"
                + "/send [JID] [MESSAGE] - send a message to [JID]\n"
                + "/list - list all devices\n"
                + "/list [JID] - list all devices for [JID]"
                + "/trust [JID] - trust the identity of [JID]\n"
                + "/clear - clear the device list\n"
                + "/regenerate - regenerate keys\n"
                + "/fingerprint - print the bots fingerprint\n"
                + "/which - print bots JID and DeviceId\n"
                + "/reload - reloads the configuration file\n"
                + "/q - closes the connection & ends the bot";
    }

    @Override
    public void handleCommand(String cmd, String jid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setOmemoController(OmemoController omemoController) {
        this.omemoController = omemoController;
    }
}