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
import de.dkwr.bompp.cmd.handler.bot.*;
import de.dkwr.bompp.util.BotConfiguration;
import de.dkwr.bompp.util.BotLogger;
import de.dkwr.bompp.xmpp.OmemoController;
import de.dkwr.bompp.util.ConfigReader;
import java.util.HashMap;
import java.util.Optional;
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
    private HashMap<String, AbstractBotCommand> botCommands;
    
    public BotCommandHandler(OmemoController omemoController, ConfigReader configReader, CommandQueue commandQueue, CommandHandler scriptCommandHandler, BareJid adminJID) {
        this.omemoController = omemoController;
        this.configReader = configReader;
        this.commandQueue = commandQueue;
        this.scriptCommandHandler = scriptCommandHandler;
        this.adminJID = adminJID;
        this.initializeCommands();
    }

    @Override
    public void handleCommand(String cmd) {
        String command = cmd.split(" ")[0];
        Optional<String> paramsOptional = this.getParamsOptional(cmd);

        if (this.botCommands.containsKey(command)) {
            try {
                this.botCommands
                        .get(command)
                        .exec(command, paramsOptional);
            } catch (Exception e) {
                System.out.println(e.getMessage());
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

        // if command does not exist AND no chat is opened print error message
        if (!this.cfg.isChatOpened()) {
            System.out.println("Error: Don't know this command.");
            return;
        }
    }

    @Override
    public String getAllCommandsAsString() {
        return "Type /help to see available commands.\n";
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
            System.out.println("Failed to send message to admin.");
            BotLogger.getInstance().logException(ex);
        }
    }

    private Optional<String> getParamsOptional(String cmd) {
        if (cmd.indexOf(' ') == -1) { // if it has only the command, then params are empty
            return Optional.empty();
        } else {  // cuts the command from the String away, to get only params
            String params = cmd.substring(cmd.indexOf(' ')).trim();
            return Optional.of(params);
        }
    }

    private void initializeCommands() {
        this.botCommands = new HashMap<>();
        HelpCommand helpCmd = new HelpCommand(this.botCommands);
        SendCmd sendCmd = new SendCmd(this.omemoController);
        ChatCommand chatCmd = new ChatCommand(this.omemoController);
        ListCommand listCmd = new ListCommand(this.omemoController);
        CloseChatCommand closeCmd = new CloseChatCommand();
        TrustCommand trustCmd = new TrustCommand(this.omemoController);
        ClearDeviceListCommand clearCmd = new ClearDeviceListCommand(this.omemoController);
        RegenerateKeysCommand regenerateCmd = new RegenerateKeysCommand(this.omemoController);
        FingerprintCommand fingerprintCmd = new FingerprintCommand(this.omemoController);
        WhichCommand whichCmd = new WhichCommand(this.omemoController);
        PrintCommandsCommand commandsCmd = new PrintCommandsCommand();
        ReloadConfigCommand reloadCmd = new ReloadConfigCommand(this.configReader);
        ExecCommand execCmd = new ExecCommand(this.scriptCommandHandler);
        QuitCommand quitCmd = new QuitCommand(this.omemoController, this.commandQueue);

        botCommands.put(helpCmd.getCommand(), helpCmd);
        botCommands.put(sendCmd.getCommand(), sendCmd);
        botCommands.put(chatCmd.getCommand(), chatCmd);
        botCommands.put(listCmd.getCommand(), listCmd);
        botCommands.put(closeCmd.getCommand(), closeCmd);
        botCommands.put(trustCmd.getCommand(), trustCmd);
        botCommands.put(clearCmd.getCommand(), clearCmd);
        botCommands.put(regenerateCmd.getCommand(), regenerateCmd);
        botCommands.put(fingerprintCmd.getCommand(), fingerprintCmd);
        botCommands.put(whichCmd.getCommand(), whichCmd);
        botCommands.put(commandsCmd.getCommand(), commandsCmd);
        botCommands.put(reloadCmd.getCommand(), reloadCmd);
        botCommands.put(execCmd.getCommand(), execCmd);
        botCommands.put(quitCmd.getCommand(), quitCmd);
    }
}
