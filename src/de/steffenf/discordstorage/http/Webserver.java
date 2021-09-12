package de.steffenf.discordstorage.http;

import de.steffenf.discordstorage.util.Logging;
import fi.iki.elonen.NanoHTTPD;
import de.steffenf.discordstorage.Main;
import de.steffenf.discordstorage.http.actions.DownloadAction;
import de.steffenf.discordstorage.http.actions.UploadAction;

import java.io.*;

@SuppressWarnings("SpellCheckingInspection")
public class Webserver extends NanoHTTPD {

    public static Webserver server;

    public Webserver(int port) throws IOException {
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        server = this;

        Logging.log("Started. Handling requests on " + port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String response;
        try {
            Logging.log("Received " + session.getMethod() + " request from " + session.getRemoteHostName() + "/" + session.getRemoteIpAddress());
            Logging.log("Requesting: " + session.getUri());
            if(session.getHeaders().get("host") == null || !Main.config.domain.contains(session.getHeaders().get("host").toLowerCase())){
                // block access for crawlers/scanners
                return newFixedLengthResponse("");
            }

            if(session.getMethod().equals(Method.PUT)){
                UploadAction action = new UploadAction();
                return action.perform(session);
            }
            if(session.getMethod().equals(Method.GET)){
                if(session.getUri().equalsIgnoreCase("/")){
                    return newFixedLengthResponse("image host");
                }
                DownloadAction action = new DownloadAction();
                return action.perform(session);
            }
            return newFixedLengthResponse("Method not implemented.");

        }catch (Exception e){
            e.printStackTrace();
            response = "error.";
        }
        return newFixedLengthResponse(response);
    }

}
