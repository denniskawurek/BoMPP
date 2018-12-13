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

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class provides a watcher for the config file. On file changes the
 * configuration is updated.
 *
 * @author Dennis Kawurek
 */
public class ConfigFileWatcher {

    private final WatchService watcher = FileSystems.getDefault().newWatchService();
    private final Map<WatchKey, Path> keys = new HashMap<>();
    private final boolean recursive = true;
    private final boolean trace = false;
    private ConfigReader configReader;
    private static ConfigFileWatcher INSTANCE;
    private boolean watch = true;
    private ExecutorService executorService;

    public ConfigFileWatcher(ConfigReader configReader) throws IOException {
        this.configReader = configReader;

        Path dir = Paths.get(BotConfiguration.getInstance().getStorePath());
        this.register(dir);
        this.INSTANCE = this;
    }

    public void watch() {
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(this::processEvents);
    }

    public static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, dir);
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents() {
        while(watch) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            key.pollEvents().forEach((event) -> {
                WatchEvent.Kind kind = event.kind();
                // TBD - provide example of how OVERFLOW event is handled
                if (!(kind == OVERFLOW)) {
                    // Context for directory entry event is the file name of entry
                    WatchEvent<Path> ev = cast(event);
                    Path name = ev.context();
                    Path child = dir.resolve(name);

                    final Path changed = ev.context();
                    if (changed.endsWith(ConfigReader.getConfigName())) {
                        if (kind == ENTRY_DELETE) {
                            BotLogger.getInstance().logMsg("Config file was deleted.");
                        }
                        if (kind == ENTRY_CREATE || kind == ENTRY_MODIFY) {
                            BotLogger.getInstance().logMsg("Config file has changed");
                            try {
                                this.configReader.reloadConfigFile();
                            } catch (Exception ex) {
                                BotLogger.getInstance().logException(ex);
                            }
                        }
                    }
                    // if directory is created, and watching recursively, then
                    // register it and its sub-directories
                    if (recursive && (kind == ENTRY_CREATE)) {
                        try {
                            if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                                register(child);
                            }
                        } catch (IOException x) {
                            // ignore to keep sample readbale
                        }
                    }
                }
            });

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    public static ConfigFileWatcher getInstance() {
        return INSTANCE;
    }

    public void stopWatching() {
        this.watch = false;
        executorService.shutdown();
    }
}
