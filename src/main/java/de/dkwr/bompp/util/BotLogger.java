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

/**
 * This class provides static methods to log exceptions and (error) messages into a file.
 * @author Dennis Kawurek
 */
public class BotLogger {
    public static void logMsg(String msg) {
        System.out.println(msg);
    }
    public static void logException(Exception ex) {
        // ToDo: Print this to the file
        System.out.println(ex.getMessage());
        /*StackTraceElement[] stElArr = ex.getStackTrace();
        for (StackTraceElement stEl : stElArr) {
            System.out.println(stEl.toString());
        }*/
        ex.printStackTrace();
    }
    
    public static void logException(Exception ex, String msg) {
        System.out.println(msg);
        logException(ex);
    }
}
