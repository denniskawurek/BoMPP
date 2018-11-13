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
import de.dkwr.bompp.cmd.exec.ExecuteScriptThread;
import de.dkwr.bompp.xmpp.OmemoController;
import de.dkwr.bompp.util.BotLogger;
import de.dkwr.bompp.util.Command;
import de.dkwr.bompp.util.CommandList;

/**
 * This CommandHandler is responsible for the execution of scripts after getting
 * a remote command.<br/>Basically he reads the config.json, makes a lookup if
 * the command and script exists, executes it and communicates with the
 * executing XMPP/Jabber account.
 *
 * @author Dennis Kawurek
 */
public class ScriptCommandHandler implements CommandHandler {

    private OmemoController omemoController;
    private final CommandList commandList;
    private final CommandQueue commandQueue;
    private final String COMMANDS_AVAILABLE_STR = "These commands are available:\n";

    /**
     * Creates a new ScriptCommandHandler object
     * @param commandQueue the commandQueue of this session
     */
    public ScriptCommandHandler(CommandQueue commandQueue) {
        this.commandList = CommandList.getInstance();
        this.commandQueue = commandQueue;
    }

    @Override
    public void setOmemoController(OmemoController omemoController) {
        this.omemoController = omemoController;
    }

    @Override
    public void handleCommand(String cmd) {
        try {
            cmd = cmd.toLowerCase();
            if (this.commandList.cmdExists(cmd)) {
                Command command = this.commandList.getCommand(cmd);
                ExecuteScriptThread executeScriptThread = new ExecuteScriptThread(command, null, true, this.omemoController);
                this.commandQueue.addToQueue(executeScriptThread);
            } else if(cmd.equalsIgnoreCase("help")) {
                System.out.println(this.COMMANDS_AVAILABLE_STR + this.getAllCommandsAsString());
            } else {
                System.out.println("This command doesn't exist.");
                System.out.println(this.COMMANDS_AVAILABLE_STR + this.getAllCommandsAsString());
            }
        } catch (Exception ex) {
            BotLogger.getInstance().logException(ex);
        }
    }

    @Override
    public void handleCommand(String cmd, String clientJID) {
        try {
            cmd = cmd.toLowerCase();
            if (this.commandList.cmdExists(cmd)) {
                Command command = this.commandList.getCommand(cmd);
                ExecuteScriptThread executeScriptThread = new ExecuteScriptThread(command, clientJID, true, this.omemoController);
                this.commandQueue.addToQueue(executeScriptThread);
            } else if(cmd.equalsIgnoreCase("help")) {
                this.omemoController.sendMessage(this.omemoController.getJid(clientJID),  this.COMMANDS_AVAILABLE_STR + this.getAllCommandsAsString());
            } else {
                this.omemoController.sendMessage(this.omemoController.getJid(clientJID), "This command doesn't exist.");
                this.omemoController.sendMessage(this.omemoController.getJid(clientJID), this.COMMANDS_AVAILABLE_STR + this.getAllCommandsAsString());
            }
        } catch (Exception ex) {
            BotLogger.getInstance().logException(ex);
        }
    }

    @Override
    public String getAllCommandsAsString() {
        return this.commandList.toString();
    }

}
