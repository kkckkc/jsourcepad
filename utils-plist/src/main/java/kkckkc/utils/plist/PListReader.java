package kkckkc.utils.plist;

import java.io.File;
import java.io.IOException;

public interface PListReader {
    Object read(File file) throws IOException;
}
