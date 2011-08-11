package kkckkc.jsourcepad.http;

import sun.net.www.MimeEntry;
import sun.net.www.MimeTable;

import java.io.File;

public abstract class AbstractServer {
    protected String getMimeEncoding(File file) {
        MimeEntry me = MimeTable.getDefaultTable().findByFileName(file.getName());
        if (me == null) {
            if (file.getName().endsWith(".css")) return "text/css";
            if (file.getName().endsWith(".js")) return "text/javascript";
            return "text/html";
        }
        return me.getType();
    }
}
