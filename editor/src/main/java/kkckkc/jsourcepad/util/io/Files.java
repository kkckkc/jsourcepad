package kkckkc.jsourcepad.util.io;

import java.io.*;

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

    public static void copy(File f, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        BufferedInputStream fis = new BufferedInputStream(new FileInputStream(f));

        int size = -1;
        while ((size = fis.read(buffer)) != -1) {
            out.write(buffer, 0, size);
        }
    }
}
