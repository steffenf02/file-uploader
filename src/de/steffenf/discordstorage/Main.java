package de.steffenf.discordstorage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.steffenf.discordstorage.fileprovider.Provider;
import de.steffenf.discordstorage.fileprovider.impl.DiscordFileProvider;
import de.steffenf.discordstorage.http.Webserver;
import de.steffenf.discordstorage.util.Config;
import de.steffenf.discordstorage.util.Logging;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Main {

    public static String outputDirectory = "output/";
    public static Gson gson = null;
    public static Config config = null;
    static String configname = "config.json";

    public static void main(String[] args) {
        gson = new GsonBuilder().setPrettyPrinting().create();
        Logging.log("Loading config...");
        loadConfig();
        Logging.log("Starting...");
        Provider.setProvider(new DiscordFileProvider());
        if(config.importDirectory.length() > 0){
            Logging.log("Import directory detected. Importing now.");
            File dir = new File(config.importDirectory);
            dir = new File(dir.getParent(), config.importDirectory);
            Logging.log(config.importDirectory);
            try {
                Files.walk(Paths.get(dir.getAbsolutePath()))
                        .filter(Files::isRegularFile)
                        .forEach(f -> {
                            try {
                                if(!Provider.fileExists(f.toFile().getName()))
                                Provider.save(f.toFile().getName(), new FileInputStream(f.toFile()));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            new Webserver(config.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadConfig(){
        try {
            InputStream is = new FileInputStream(new File(configname));
            config = gson.fromJson(new InputStreamReader(is), Config.class);
        } catch (FileNotFoundException e) {
            Logging.log("File not found. Creating a new one.");
            config = new Config();
            config.discordToken = "token";
            config.map = new HashMap<>();
            config.map.put("example", "values");
            config.importDirectory = "";
            config.port = 8080;
            saveConfig();
            e.printStackTrace();
        }
    }

    public static void saveConfig(){
        String json = gson.toJson(config);
        try {
            FileWriter fw = new FileWriter(configname);
            fw.write(json);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
