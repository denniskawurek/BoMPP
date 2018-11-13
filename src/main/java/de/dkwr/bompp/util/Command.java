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

import java.util.LinkedList;
import java.util.List;

/**
 * Represents the script command which can be executed by a client.
 * @author Dennis Kawurek
 */
public class Command {

    private final String cmd;
    private final String scriptPath;
    private final String execType;
    private final String description;
    private final boolean collectOutput;
    
    public Command(String cmd, String scriptPath, String execType, String description, boolean collectOutput) {
        this.cmd = cmd;
        this.scriptPath = scriptPath;
        this.execType = execType;
        this.description = description;
        this.collectOutput = collectOutput;
    }

    public String getCommandName() {
        return this.cmd;
    }

    public boolean getCollectOutput() {
        return this.collectOutput;
    }

    public String getScriptPath() {
        return this.scriptPath;
    }

    public String getExecType() {
        return this.execType;
    }
    
    @Override
    public String toString() {
        return this.cmd + " " + this.description + " " + this.collectOutput;
    }
    
    /**
     * Returns command execution list
     * @return execution list {exec type, script path} or {script path}
     */
    public List<String> getCmdExecutionList() {
        List<String> l = new LinkedList<>();
        if(this.execType != null) {
            l.add(execType);
            l.add(scriptPath);
        } else l.add(scriptPath);
        
        return l;
    }
}
