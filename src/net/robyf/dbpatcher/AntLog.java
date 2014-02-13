package net.robyf.dbpatcher;

public final class AntLog implements Log {

    private final AntLauncher launcher;

    public AntLog(final AntLauncher launcher) {
        this.launcher = launcher;
    }

    @Override
    public void log(final String message) {
        this.launcher.log(message);
    }

}
