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
package net.robyf.dbpatcher.util;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @since 0.9.0
 * @author Roberto Fasciolo
 */
public final class Column {

    private final String name;
    private final String type;
    private final int size;
    private final boolean nullable;

    Column(final ResultSetMetaData metadata, final int index) throws SQLException {
        this.name = metadata.getColumnName(index);
        this.type = metadata.getColumnTypeName(index);
        this.size = metadata.getColumnDisplaySize(index);
        this.nullable = (metadata.isNullable(index) == 1);
    }

    public Column(final String name, final String type, final int size, final boolean nullable) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.nullable = nullable;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public boolean isNullable() {
        return nullable;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (nullable ? 1231 : 1237);
        result = prime * result + size;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Column)) {
            return false;
        }
        Column other = (Column) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (nullable != other.nullable) {
            return false;
        }
        if (size != other.size) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "[column name='" + this.name + "', type='" + this.type + "', size=" + this.size
               + ", nullable=" + this.nullable + "]";
    }

}
