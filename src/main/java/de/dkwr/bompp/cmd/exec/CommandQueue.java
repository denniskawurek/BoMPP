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
package de.dkwr.bompp.cmd.exec;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author Dennis Kawurek
 */
public class CommandQueue {

    //private final ThreadPoolExecutor threadPool;
    private final ExecutorService threadPool;
    HashMap threads;

    /**
     *
     * @param nThreads
     * @param blockingQueueSize
     */
    public CommandQueue(int nThreads, int blockingQueueSize) {
        this.threads = new HashMap<String, Future<?>>();
        this.threadPool = Executors.newFixedThreadPool(nThreads);
    }

    /**
     * Adds a new Task to the queue, which will be executed as soon as there is
     * a free place in the thread pool.
     *
     * @param cmd Command name to execute
     * @param executeScriptThread new Task to execute.
     * @return true if command is added to queue, false if not. A reason can be that a command is already running.
     */
    public boolean addToQueue(String cmd, ExecuteScriptThread executeScriptThread) {
        if (!this.threads.containsKey(cmd)) {
            Future<?> f = this.threadPool.submit(executeScriptThread);
            this.threads.put(cmd, f);
            return true;
        } else {
            return checkIfCommandFinishedAndAddToQueue(cmd, executeScriptThread);
        }
    }
    
    private boolean checkIfCommandFinishedAndAddToQueue(String cmd, ExecuteScriptThread executeScriptThread) {
        Future<?> thread = (Future) this.threads.get(cmd);
        if(thread.isDone()) {
            Future<?> f = this.threadPool.submit(executeScriptThread);
            this.threads.put(cmd, f);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Waits until current running tasks are finished and new tasks are not
     * accepted anymore.
     */
    public void quitCommandExecution() {
        this.threadPool.shutdown();
    }

    /**
     * Stops immediately all current running tasks.
     */
    public void quitCommandExecutionNow() {
        this.threadPool.shutdownNow();
    }
}
