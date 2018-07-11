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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides static methods to log exceptions and (error) messages
 * into a file. BotLogger is a Singleton.
 *
 * @author Dennis Kawurek
 */
public class BotLogger {

    private static final BotLogger instance = new BotLogger();
    private static final BotConfiguration cfg = BotConfiguration.getInstance();
    private static final String logFileName = ".log";
    private static final String logDateFormat = "dd.MM. HH:mm:ss";

    private BotLogger() {
        super();
    }

    public synchronized void logMsg(String msg) {
        String dateStr = currentTimeStr();
        try (FileWriter fw = new FileWriter(cfg.getStorePath() + logFileName, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter printWriter = new PrintWriter(bw)) {
            printWriter.println(dateStr + "" + msg);
        } catch (IOException ex) {
            Logger.getLogger(BotLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void logException(Exception exception) {
        exception.printStackTrace();
        String dateStr = currentTimeStr();
        try (FileWriter fw = new FileWriter(cfg.getStorePath() + logFileName, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter printWriter = new PrintWriter(bw)) {
            printWriter.println(dateStr + "Stacktrace:");
            exception.printStackTrace(printWriter);
        } catch (IOException ex) {
            Logger.getLogger(BotLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Get the instance.
     *
     * @return BotLogger instance
     */
    public static BotLogger getInstance() {
        return instance;
    }

    private static String currentTimeStr() {
        Date now = Calendar.getInstance().getTime();
        Format formatter = new SimpleDateFormat(logDateFormat);
        return "[" + formatter.format(now) + "]: ";
    }
}
