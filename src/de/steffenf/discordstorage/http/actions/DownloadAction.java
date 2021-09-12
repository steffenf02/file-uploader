package de.steffenf.discordstorage.http.actions;

import de.steffenf.discordstorage.fileprovider.Provider;
import fi.iki.elonen.NanoHTTPD;
import de.steffenf.discordstorage.http.Action;
import de.steffenf.discordstorage.util.MimeTypes;

import java.io.IOException;
import java.io.InputStream;

public class DownloadAction extends Action {
    @Override
    public NanoHTTPD.Response perform(NanoHTTPD.IHTTPSession session) {
        InputStream output = Provider.load(session.getUri().substring(1));

        String extension = "";

        int i = session.getUri().substring(1).lastIndexOf('.');
        if (i > 0) {
            extension = session.getUri().substring(1).substring(i+1);
        }
        try {
            if(output == null){
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "");
            }
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, MimeTypes.lookupMimeType(extension), output, output.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
