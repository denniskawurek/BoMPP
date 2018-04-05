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
import de.dkwr.bompp.commandexecutor.ExecuteScriptThread;
import de.dkwr.bompp.omemo.OmemoController;
import de.dkwr.bompp.util.BotLogger;
import de.dkwr.bompp.util.CommandList;

/**
 * This CommandHandler is responsible for the execution of scripts after getting
 * a remote command.<br/>Basically he reads the config.json, makes a lookup if
 * the command and script exists, executes it and communicates with the
 * executing XMPP/Jabber account.
 *
 * @author Dennis Kawurek
 */
public class ScriptCommandHandler extends CommandHandler {

    private OmemoController omemoController;
    private final CommandList commandList;
    private final CommandQueue commandQueue;

    /**
     * Creates a new ScriptCommandHandler object
     * @param commandList the commandList of this session
     * @param commandQueue the commandQueue of this session
     */
    public ScriptCommandHandler(CommandList commandList, CommandQueue commandQueue) {
        this.commandList = commandList;
        this.commandQueue = commandQueue;
    }

    @Override
    public void setOmemoController(OmemoController omemoController) {
        this.omemoController = omemoController;
    }

    @Override
    public void handleCommand(String cmd) {}

    @Override
    public void handleCommand(String cmd, String clientJID) {
        try {
            if (!this.commandList.cmdExists(cmd)) {
                this.omemoController.sendMessage(clientJID, "This command doesn't exist.");
                this.omemoController.sendMessage(clientJID, "These commands are available:\n" + this.getAllCommandsAsString());
            } else {
                String[] cmdArr = cmd.split(" ");
                String[] cmdDetails = this.commandList.getCommand(cmdArr[0]);
                String scriptPath = cmdDetails[0];
                
                cmdArr[0] = scriptPath; // replaces the command with the path
                
                ExecuteScriptThread executeScriptThread = new ExecuteScriptThread(cmdArr, clientJID, true, this.omemoController);
                this.commandQueue.addToQueue(executeScriptThread);
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
