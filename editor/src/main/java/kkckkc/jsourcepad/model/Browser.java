package kkckkc.jsourcepad.model;

import java.io.IOException;
import java.net.URI;

public interface Browser {
    public void show(URI url, boolean includeNavigation) throws IOException, IOException;
    public boolean isExternal();
}
