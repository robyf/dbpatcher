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
 * Factory for DBPatcher objects.
 * 
 * @since 0.9.0
 * @author Roberto Fasciolo
 */
public final class DBPatcherFactory {

    private static DBPatcher patcher = null;

    private DBPatcherFactory() {
    }

    /**
     * Creates a new DBPatcher instances.
     * 
     * @return a DBPatcher instance
     * @throws DBPatcherException If an error happens while creating the instance
     */
    public static DBPatcher getDBPatcher() throws DBPatcherException {
        if (patcher != null) {
            return patcher;
        }
        return new DBPatcherImpl();
    }

    /**
     * Instructs this factory to use an alternate implementation of DBPatcher. For unit testing
     * purposes only.
     * 
     * @param patcher The alternate DBPatcher implementation
     */
    public static void setDBPatcher(final DBPatcher patcher) {
        DBPatcherFactory.patcher = patcher;
    }

    /**
     * Instructs this factory to revert to the default DBPatcher implementation. For unit testing
     * purposes only.
     */
    public static void reset() {
        DBPatcherFactory.patcher = null;
    }

}
