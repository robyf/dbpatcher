package net.robyf.dbpatcher;

public final class ConsoleLog implements Log {

    @Override
    public void log(final String message) {
        System.out.println(message);
    }

}
