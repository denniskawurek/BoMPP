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

import de.dkwr.bompp.omemo.OmemoController;

/**
 * Abstract class for a command handler. All command handlers have to extend this class.
 * @author Dennis Kawurek
 */
public interface CommandHandler {
    /**
     * Handles a command by a Client, who doesn't expect an answer (e.g. bot administrator).
     * @param cmd command
     */
    void handleCommand(String cmd);
    
    /**
     * Handles a command by a Client, who also expects an answer.
     * @param cmd
     * @param jid 
     */
    void handleCommand(String cmd, String jid);
    
    /**
     * Returns all available and implemented commands of this CommandHandler as a String (can be used for a 'help' command).
     * @return 
     */
    abstract String getAllCommandsAsString();
    
    /**
     * Sets a new OmemoController
     * @param omemoController the OmemoController for the current session.
     */
    abstract void setOmemoController(OmemoController omemoController);
}
