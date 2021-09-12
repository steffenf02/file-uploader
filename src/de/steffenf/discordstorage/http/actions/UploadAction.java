package de.steffenf.discordstorage.http.actions;

import de.steffenf.discordstorage.fileprovider.Provider;
import de.steffenf.discordstorage.util.Logging;
import fi.iki.elonen.NanoHTTPD;
import de.steffenf.discordstorage.http.Action;

import java.io.ByteArrayInputStream;

public class UploadAction extends Action {
    @Override
    public NanoHTTPD.Response perform(NanoHTTPD.IHTTPSession session) {
        String fileName = session.getHeaders().get("filename");
        String originalFileName = fileName;
        String extension = "";
        int i = fileName.substring(1).lastIndexOf('.');
        if (i > 0) {
            extension = "." + fileName.substring(1).substring(i+1);
        }
        String filenameWithoutExtension = originalFileName.replace(extension, "");
        int filecount = 0;
        if (session.getHeaders().get("content-length") != null) {
            int contentLength = Integer.parseInt(session.getHeaders().get("content-length"));
            byte[] buf = new byte[contentLength];
            try {
                boolean readingDone = false;
                int currentLength = 0;
                while(!readingDone){
                    int readBytes = session.getInputStream().read(buf, currentLength, contentLength - currentLength);
                    currentLength += readBytes;
                    if(currentLength >=contentLength){
                        readingDone = true;
                    }
                }
                Logging.debug("read " + currentLength + "/" + buf.length);
                Logging.log("Saving...");
                // hacky way to handle duplicate file names
                while(Provider.fileExists(fileName)){
                    fileName = filenameWithoutExtension + filecount + extension;
                    filecount++;
                    if(!Provider.fileExists(fileName)){
                        Logging.log("New Filename: " + fileName);
                    }
                }
                Provider.save(fileName, new ByteArrayInputStream(buf));
                Logging.log("Saved.");
            } catch (Exception e) {
            }
        }
        return NanoHTTPD.newFixedLengthResponse(fileName);
    }
}
