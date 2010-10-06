package kkckkc.utils.io;

import java.io.File;
import java.io.IOException;

public class FileUtils {
	public static File newTempFile(String prefix, String suffix) throws IOException {
		File f = File.createTempFile(prefix, suffix);
		f.deleteOnExit();
		return f;
	}
}
