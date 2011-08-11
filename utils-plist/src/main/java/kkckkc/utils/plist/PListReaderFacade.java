package kkckkc.utils.plist;

import kkckkc.utils.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class PListReaderFacade implements PListReader {

	@Override
    public Object read(File file) throws IOException {
		byte[] bytes = FileUtils.readBytes(file);

		if (bytes[0] == (byte) 'b' && bytes[1] == (byte) 'p') {
			return new BinaryPListReader().read(bytes);
		} else if (bytes[0] == (byte) '<' && bytes[1] == (byte) '?') {
			return new XMLPListReader().read(bytes);
		} else {
			return new LegacyPListReader().read(bytes);
		}
	}

}
