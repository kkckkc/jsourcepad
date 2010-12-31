package kkckkc.jsourcepad.model;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class SystemBrowser implements Browser {
    @Override
    public void show(URI url, boolean includeNavigation) throws IOException {
        Desktop.getDesktop().browse(url);
    }

    @Override
    public boolean isExternal() {
        return true;
    }
}
