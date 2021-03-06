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
package net.robyf.dbpatcher.parser;

/**
 * A runtime exception throw when an unexpected situation happens while parsing a SQL script.
 * 
 * @since 0.9.0
 * @author Roberto Fasciolo
 */
public final class ParsingException extends RuntimeException {

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public ParsingException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
