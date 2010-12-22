package kkckkc.utils.plist;

import kkckkc.utils.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class GeneralPListReader implements PListReader {

	public Object read(File file) throws IOException {
		byte[] dest = FileUtils.readBytes(file);

		if (dest[0] == (byte) 'b' && dest[1] == (byte) 'p') {
			return new NIOBinaryPListReader().read(dest);
		} else if (dest[0] == (byte) '<' && dest[1] == (byte) '?') {
			return new NIOXMLPListReader().read(dest);
		} else {
			return new NIOLegacyPListReader().read(dest);
		}
	}




}
