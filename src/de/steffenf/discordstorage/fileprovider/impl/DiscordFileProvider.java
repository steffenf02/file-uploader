package de.steffenf.discordstorage.fileprovider.impl;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import de.steffenf.discordstorage.Main;
import de.steffenf.discordstorage.fileprovider.FileProvider;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.AttachmentOption;
import net.dv8tion.jda.internal.utils.IOUtil;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DiscordFileProvider extends FileProvider {

    public static JDA jda;
    private static String guildId = "604762523970830387";
    private static String channelId = "604764162035548170";

    private Guild guild = null;
    private TextChannel channel = null;

    @Override
    public void startUp() {
        try {
            jda = new JDABuilder().setToken(Main.config.discordToken).setStatus(OnlineStatus.DO_NOT_DISTURB).build();
            jda.awaitReady();
            guild = jda.getGuildById(guildId);
            assert guild != null;
            channel = guild.getTextChannelById(channelId);
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean save(String filename, InputStream content) {
        Message msg = channel.sendFile(content, filename, AttachmentOption.SPOILER).append(filename).complete();
        Main.config.map.put(filename, msg.getChannel().getId() + "/" + msg.getId());
        Main.saveConfig();
        return true;
    }

    @Override
    public InputStream load(String filename) {
        if(Main.config.map.containsKey(filename)){
            try {
                Message message = channel.retrieveMessageById(Main.config.map.get(filename).replace(channelId + "/", "")).complete();
                CompletableFuture<InputStream> future = message.getAttachments().get(0).retrieveInputStream();
                while(!future.isDone()){
                    Thread.sleep(50);
                }
                BufferedInputStream fileStream = (BufferedInputStream) future.get(30, TimeUnit.SECONDS);
                byte[] fileBytes = IOUtil.readFully(fileStream);
                fileStream.read(fileBytes);
                return new ByteInputStream(fileBytes, fileBytes.length);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean fileExists(String filename) {
        return Main.config.map.containsKey(filename);
    }

    @Override
    public void delete(String filename) {
        // todo: maybe use implementation from below, will need more testing
    }

    /*@Override
    public void delete(String filename) {
        if(Main.config.map.containsKey(filename)){
            Main.config.map.remove(filename);
            Main.saveConfig();
        }
    }*/
}
