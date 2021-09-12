package de.steffenf.discordstorage.fileprovider;

import java.io.InputStream;

public class Provider {

    private static FileProvider provider;

    public static void save(String filename, InputStream content){
        provider.save(filename, content);
    }

    public static InputStream load(String filename){
        return provider.load(filename);
    }

    public static boolean fileExists(String filename) {
        return provider.fileExists(filename);
    }

    public static void setProvider(FileProvider provider){
        Provider.provider = provider;
        provider.startUp();
    }

    public static FileProvider getProvider(){
        return provider;
    }
}
