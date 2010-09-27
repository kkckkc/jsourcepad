package kkckkc.jsourcepad.util.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Files {
	public static File newTempFile(String prefix, String suffix) throws IOException {
		File f = File.createTempFile(prefix, suffix);
		f.deleteOnExit();
		return f;
	}
	
	public static void write(File file, CharSequence cs) throws IOException {
		FileWriter fw = new FileWriter(file);
	    fw.write(cs.toString());
	    fw.close();		
	}
}
