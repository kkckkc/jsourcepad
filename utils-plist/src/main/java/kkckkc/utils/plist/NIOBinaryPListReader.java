package kkckkc.utils.plist;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class NIOBinaryPListReader {
    private static Logger logger = Logger.getLogger(NIOBinaryPListReader.class.toString());

	private List<Object> objects;

	public Object read(byte[] bytes) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);

        checkFileFormat(buffer);
        checkFileFormatVersion(buffer);

        long refCount = getRefCount(buffer);
		boolean useShortsForRefs = refCount >= 256;

        // Start reading the objects
		buffer.position(8);
		
		this.objects = new ArrayList<Object>();
		for (int i = 0; i <= refCount; i++) {
			int data = unsignedByteToInt(buffer.get());

            // The recordType is found in the high order nibble
			int recordType = (data & 0xF0) >> 4;

            switch (recordType) {
                case 0:
                    if ((data & 0xF) == 15) continue;
				    objects.add(readSimple(data));
                    break;
                case 1:
                    objects.add(readInteger(data, buffer));
                    break;
                case 2:
                    objects.add(readReal(data, buffer));
                    break;
                case 4:
                    objects.add(readData(data, buffer));
                    break;
                case 5:
				    objects.add(readString(data, buffer));
                    break;
                case 6:
                    objects.add(readUnicodeString(data, buffer));
                    break;
                case 10:
                    objects.add(readArray(data, buffer, useShortsForRefs));
                    break;
                case 13:
                    objects.add(readDictionary(data, buffer, useShortsForRefs));
                    break;
                default:
                    logger.severe("Unsupported recordType " + recordType);
            }
		}
		
		return resolve(objects.get(0));
	}

    private long getRefCount(ByteBuffer buffer) {
        int length = buffer.remaining();
        buffer.position(length - 32);
        buffer.getLong();
        return buffer.getLong();
    }

    private void checkFileFormatVersion(ByteBuffer buffer) {
        String version = readAsciiString(buffer, 6, 2);
        if (! "00".equals(version)) {
            throw new RuntimeException("Incorrect fileformat version");
        }
    }

    private void checkFileFormat(ByteBuffer buffer) {
        String fileFormat = readAsciiString(buffer, 0, 6);
        if (! "bplist".equals(fileFormat)) {
            throw new RuntimeException("Incorrect fileformat");
        }
    }

    public final int unsignedByteToInt(byte b) {
		return (int)b & 0xFF;
	}

	private Object readData(int data, ByteBuffer buffer) throws IOException {
        int count = readCount(data, buffer);
        byte[] bytes = new byte[count];
        buffer.get(bytes);
        return bytes;    
    }

	private Object readReal(int data, ByteBuffer buffer) throws IOException {
		int count = 1 << (data & 0xf);
        switch (count) {
        case 4 :
            return buffer.getFloat();
        case 8 :
            return buffer.getDouble();
        default :
            throw new IOException("parseReal: unsupported byte count:"+count);
        }
    }

	private Object readInteger(int data, ByteBuffer buffer) {
		int byteCount = 1 << (data & 0xf);
		int integer = 0;
		for (int i = 0; i < byteCount; i++) {
			int b = unsignedByteToInt(buffer.get());
			integer = (integer << 8) | b;
		}

		return integer;
	}

	private String readUnicodeString(int data, ByteBuffer buffer) throws IOException {
		int count = readCount(data, buffer);
		
		char[] chars = new char[count];
		for (int i = 0; i < count; i++) {
			chars[i] = buffer.getChar();
		}
		return new String(chars);
	}

	private Object readArray(int data, ByteBuffer buffer, final boolean readShorts) throws IOException {
		int count = readCount(data, buffer);
		
		Ref[] array = new Ref[count];
		for (int i = 0; i < count; i++) {
			if (readShorts) {
				array[i] = new Ref(buffer.getShort());
			} else {
				array[i] = new Ref(unsignedByteToInt(buffer.get()));
			}
		}
		
		return new RefCollection(RefCollection.ARRAY, array);
	}

	private int readCount(int data, ByteBuffer buffer) {
		int count = (data & 0xF); 
		if (count == 15) {
			int m = unsignedByteToInt(buffer.get());
			int byteCount = 1 << (m & 0xf);
			count = 0;
			for (int i = 0; i < byteCount; i++) {
				int b = unsignedByteToInt(buffer.get());
				count = (count << 8) | b;
			}
		}
		return count;
	}

	private Object readSimple(int data) {
		int type = data & 0xF;
		if (type == 0) return null;
		else if (type == 8) return Boolean.FALSE;
		else if (type == 9) return Boolean.TRUE;
		throw new IllegalStateException("Cannot understand simple type " + type);
	}

	private Object resolve(Object object) {
		if (object instanceof Ref) {
			return resolveRef((Ref) object);
			
		} else if (object instanceof RefCollection) {
			
			RefCollection rc = (RefCollection) object;
			if (rc.getType() == RefCollection.ARRAY) {
				return resolveList(rc);
			} else {
				return resolveMap(rc);
			}
			
		} else {
			return object;
		}
	}

	private Object resolveRef(Ref object) {
	    return resolve(object.resolve());
    }

	private List<Object> resolveList(RefCollection rc) {
		List<Object> objectList = new ArrayList<Object>(rc.getRefs().length);
		for (Ref ref : rc.getRefs()) {
			objectList.add(resolve(ref.resolve()));
		}
		return objectList;
	}

	private Map<Object, Object> resolveMap(RefCollection rc) {
		int refCount = rc.getRefs().length / 2;
		Map<Object, Object> map = new LinkedHashMap<Object, Object>(refCount);
		
		Ref[] refs = rc.getRefs();
		for (int i = 0; i < refCount; i++) {
			Ref key = refs[i * 2];
			Ref value = refs[(i * 2) + 1];
			map.put(resolve(key.resolve()), resolve(value.resolve()));
		}
		
		return map;
	}
	
	private String readString(int data, ByteBuffer buffer) throws IOException {
		int count = readCount(data, buffer);

		byte[] bytes = new byte[count];
		buffer.get(bytes);
		return new String(bytes);
	}

	private Object readDictionary(int data, ByteBuffer buffer, final boolean readShorts) throws IOException {
		int count = readCount(data, buffer);
		
		Ref[] refs = new Ref[count * 2];
		for (int i = 0; i < count; i++) {
			if (readShorts) {
				refs[i * 2] = new Ref(buffer.getShort());
			} else {
				refs[i * 2] = new Ref(unsignedByteToInt(buffer.get()));
			}
		}
		
		for (int i = 0; i < count; i++) {
			if (readShorts) {
				refs[(i * 2) + 1] = new Ref(buffer.getShort());
			} else {
				refs[(i * 2) + 1] = new Ref(unsignedByteToInt(buffer.get()));
			}
		}
		
		return new RefCollection(RefCollection.DICTIONARY, refs);
	}

	private String readAsciiString(ByteBuffer buffer, int offset, int length) {
		byte[] bytes = new byte[length];
		buffer.position(offset);
		buffer.get(bytes);
		return new String(bytes);
	}
	

	final class Ref {
		private int offset;

		public Ref(int offset) {
			this.offset = offset;
		}
		
		public String toString() {
			return "ref(" + offset + ")";
		}
		
		public Object resolve() {
			return objects.get(offset);
		}
	}
	
	final static class RefCollection {
		public static final int ARRAY = 1, DICTIONARY = 2;
		
		private int type;
		private Ref[] refs;
		
		public RefCollection(int type, Ref[] refs) {
	        this.type = type;
	        this.refs = refs;
        }
		
		public int getType() {
	        return type;
        }
		
		public Ref[] getRefs() {
	        return refs;
        }
	}
}
