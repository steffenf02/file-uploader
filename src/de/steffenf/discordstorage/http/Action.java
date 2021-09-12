package de.steffenf.discordstorage.http;

import fi.iki.elonen.NanoHTTPD;

public abstract class Action {

    public abstract NanoHTTPD.Response perform(NanoHTTPD.IHTTPSession session);

}
