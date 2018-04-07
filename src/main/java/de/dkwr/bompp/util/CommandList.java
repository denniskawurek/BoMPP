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
package de.dkwr.bompp.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a list of available commands. The CommandList is a Singleton.<br/>
 *
 * @author Dennis Kawurek
 */
public class CommandList {

    private HashMap<String, String[]> cmdMap = new HashMap<>();
    private static final CommandList instance = new CommandList();
    
    private CommandList() {
    }
    
    
    public static CommandList getInstance() {
        return instance;
    }
    
    /**
     * Clears the command list.
     */
    public void clear() {
        this.cmdMap.clear();
    }

    /**
     * Checks whether the command exists or not.
     *
     * @param cmd the command according to 'cmd' field in the config.json file
     * @return true if it exists, false if not
     */
    public boolean cmdExists(String cmd) {
        return this.cmdMap.containsKey(cmd);
    }

    /**
     * Adds a new command to the list
     *
     * @param cmd command
     * @param script absolute path to the script
     * @param exec_type script type
     * @param description description of the script
     */
    public void addCommand(String cmd, String script, String exec_type, String description) {
        this.cmdMap.put(cmd, new String[]{script, exec_type, description});
    }

    /**
     * Returns the details for a command.
     *
     * @param cmd the command
     * @return a String[] array containing three objects:<br/>
     * <ul>
     * <li>Script path</li>
     * <li>Script execution type</li>
     * <li>Description</li>
     * </ul>
     */
    public String[] getCommand(String cmd) {
        return this.cmdMap.get(cmd);
    }

    public int getSize() {
        return this.cmdMap.size();
    }

    @Override
    public String toString() {
        StringBuilder cmdStr = new StringBuilder();

        for (Map.Entry<String, String[]> entry : this.cmdMap.entrySet()) {
            cmdStr.append(entry.getKey());
            cmdStr.append(": ");
            cmdStr.append(entry.getValue()[2]);
            cmdStr.append("\n");
        }

        return cmdStr.toString();
    }
}
