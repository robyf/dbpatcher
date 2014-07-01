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
package net.robyf.dbpatcher.schema;

/**
 * A directory containg a database version.
 * 
 * @author Roberto Fasciolo
 * @since 0.9.1
 */
final class VersionDir implements Comparable<VersionDir> {

    private final Long version;
    private final String dirName;

    public VersionDir(final Long version, final String dirName) {
        this.version = version;
        this.dirName = dirName;
    }

    public Long getVersion() {
        return version;
    }

    public String getDirName() {
        return dirName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dirName == null) ? 0 : dirName.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
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
        if (!(obj instanceof VersionDir)) {
            return false;
        }
        VersionDir other = (VersionDir) obj;
        if (dirName == null) {
            if (other.dirName != null) {
                return false;
            }
        } else if (!dirName.equals(other.dirName)) {
            return false;
        }
        if (version == null) {
            if (other.version != null) {
                return false;
            }
        } else if (!version.equals(other.version)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(final VersionDir o) {
        return this.getVersion().compareTo(o.getVersion());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("VersionDir [version=");
        builder.append(version);
        builder.append(", dirName=");
        builder.append(dirName);
        builder.append("]");
        return builder.toString();
    }

}
