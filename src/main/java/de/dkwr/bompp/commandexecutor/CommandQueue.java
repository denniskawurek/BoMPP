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
package de.dkwr.bompp.commandexecutor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author Dennis Kawurek
 */
public class CommandQueue {
    private final ThreadPoolExecutor threadPool;
    
    /**
     * 
     * @param nThreads
     * @param blockingQueueSize 
     */
    public CommandQueue(int nThreads, int blockingQueueSize) {
        RejectedExecutionHandlerImpl rejectionHandler = new RejectedExecutionHandlerImpl();
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        
        BlockingQueue queue = new ArrayBlockingQueue<>(blockingQueueSize);
        
        this.threadPool = new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS, queue, threadFactory, rejectionHandler);
    }
    
    /**
     * Adds a new Task to the queue, which will be executed as soon as there is a free place in the thread pool.
     * @param executeScriptThread new Task to execute.
     */
    public void addToQueue(ExecuteScriptThread executeScriptThread) {
        this.threadPool.execute(executeScriptThread);
    }
    
    /**
     * Waits until current running tasks are finished and new tasks are not accepted anymore.
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
