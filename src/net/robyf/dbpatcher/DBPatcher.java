package net.robyf.dbpatcher;


public interface DBPatcher {
    
    void patch(Parameters parameters) throws DBPatcherException;

}
