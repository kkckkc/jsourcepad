package kkckkc.syntaxpane.util.plist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeneralPListReader implements PListReader {

	public Object read(File file) throws IOException {
		byte[] dest = readBytes(file);
		
		if (dest[0] == (byte) 'b' && dest[1] == (byte) 'p') {
			return new NIOBinaryPListReader().read(dest);
		} else if (dest[0] == (byte) '<' && dest[1] == (byte) '?') {
			return new NIOXMLPListReader().read(dest);
		} else {
			return new NIOLegacyPListReader().read(dest);
		}
	}


	private byte[] readBytes(File file) throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(file);

		byte[] dest = new byte[(int) fis.available()];
		fis.read(dest);
		fis.close();
		
		return dest;
    }


	public static void main(String... args) throws IOException {
		GeneralPListReader r = new GeneralPListReader();
		
		for (int i = 0; i < 10; i++) {
			long l = System.currentTimeMillis();
			
			List<File> dest = new ArrayList<File>(500);
			recurse(new File("/home/magnus/Desktop/Dropbox/SharedSupport/Bundles"), dest);
			
			for (File f : dest) {
				r.read(f);
			}
			
			System.out.println(System.currentTimeMillis() - l);
		}
	}
	

	private static void recurse(File file, List<File> dest) {
		for (File f : file.listFiles()) {
			String name = f.getName();
			if (name.endsWith(".plist") || name.endsWith(".tmLanguage")) {
				dest.add(f);
			} else if (f.isDirectory()) {
				recurse(f, dest);
			}
		}
	}
	
}
