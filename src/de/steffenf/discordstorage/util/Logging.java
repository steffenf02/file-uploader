package de.steffenf.discordstorage.util;

import de.steffenf.discordstorage.Main;

public class Logging {

    public static void log(String log){
        System.out.println(log);
    }

    public static void debug(String log){
        if(Main.config.debug){
            System.out.println(log);
        }
    }

}
