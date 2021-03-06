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

import java.util.Scanner;

/**
 * This class provides a static Scanner Object which can be used in the whole program.
 * @author Dennis Kawurek
 */
public class StaticScanner {
    public static Scanner scanner = new Scanner(System.in);

    public static void close() {
        scanner.close();
    }
}
