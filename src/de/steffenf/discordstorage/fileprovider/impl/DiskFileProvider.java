package de.steffenf.discordstorage.fileprovider.impl;

import de.steffenf.discordstorage.Main;
import de.steffenf.discordstorage.fileprovider.FileProvider;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DiskFileProvider extends FileProvider {

    @Override
    public void startUp() {}

    @Override
    public boolean save(String filename, InputStream content) {
        File output = new File(Main.outputDirectory + "/", filename);
        try {
            Files.copy(content, Paths.get(output.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public InputStream load(String filename) {
        try {
            InputStream is = new FileInputStream(new File(Main.outputDirectory, filename));
            return is;
        } catch (FileNotFoundException e) {

        }
        return null;
    }

    @Override
    public boolean fileExists(String filename) {
        return new File(Main.outputDirectory, filename).exists();
    }

    @Override
    public void delete(String filename) {
        try {
            Files.delete(new File(Main.outputDirectory + "/", filename).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
