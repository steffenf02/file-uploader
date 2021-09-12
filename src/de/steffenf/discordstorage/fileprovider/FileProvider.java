package de.steffenf.discordstorage.fileprovider;

import java.io.InputStream;

public abstract class FileProvider {

    public abstract void startUp();

    public abstract boolean save(String filename, InputStream content);

    public abstract InputStream load(String filename);

    public abstract boolean fileExists(String filename);

    public abstract void delete(String filename);

}
