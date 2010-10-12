package kkckkc.utils.io;

import java.io.File;
import java.io.IOException;

public class FileUtils {
	public static File newTempFile(String prefix, String suffix) throws IOException {
		File f = File.createTempFile(prefix, suffix);
		f.deleteOnExit();
		return f;
	}

    public static boolean isAncestorOf(File descendant, File ancestor) {
        if (ancestor.equals(descendant)) {
            return false;
        }

        while (descendant != null && ! ancestor.equals(descendant)) {
            descendant = descendant.getParentFile();
        }

        return descendant != null;
    }
}
