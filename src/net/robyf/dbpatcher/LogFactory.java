/*
 * Copyright 2014 Roberto Fasciolo
 * 
 * This file is part of dbpatcher.
 * 
 * dbpatcher is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * dbpatcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with dbpatcher; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.robyf.dbpatcher;

/**
 * @since 0.9.0
 * @author Roberto Fasciolo
 */
public final class LogFactory {

    private static Log log = null;

    private LogFactory() {
    }

    public static Log getLog() {
        if (LogFactory.log != null) {
            return LogFactory.log;
        }
        return new ConsoleLog();
    }

    public static void setLog(final Log log) {
        LogFactory.log = log;
    }

}
