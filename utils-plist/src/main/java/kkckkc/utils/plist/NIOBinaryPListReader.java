package kkckkc.utils.plist;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NIOBinaryPListReader {


	private List<Object> objects;


	public Object read(byte[] bytes) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);

		int length = buffer.remaining();
		
		String s = readAsciiString(buffer, 0, 6);
		if (! "bplist".equals(s)) {
			throw new RuntimeException("Incorrect fileformat");
		}
		
		s = readAsciiString(buffer, 6, 2);
		if (! "00".equals(s)) {
			throw new RuntimeException("Incorrect fileformat version");
		}

		buffer.position(length - 32);
		buffer.getLong();
		long refCount = buffer.getLong();
		
		buffer.position(8);
		
		List<Object> objects = new ArrayList<Object>();
		for (int i = 0; i <= refCount; i++) {
			int data = fb(buffer.get());
			
			int recordType = (data & 0xF0) >> 4;
		
			if (recordType == 0) {
				if ((data & 0xF) == 15) continue;
				objects.add(readSimple(data, buffer));
			} else if (recordType == 1) {
				objects.add(readInteger(data, buffer));
			} else if (recordType == 2) {
				objects.add(readReal(data, buffer));
			} else if (recordType == 4) {
                objects.add(readData(data, buffer));
			} else if (recordType == 5) {
				Object o = readString(data, buffer);
				objects.add(o);
			} else if (recordType == 6) {
				objects.add(readUnicodeString(data, buffer));
			} else if (recordType == 10) {
				objects.add(readArray(data, buffer, refCount >= 256));
			} else if (recordType == 13) {
				objects.add(readDictionary(data, buffer, refCount >= 256));
			} else {
				System.out.println("Unsupported recordType " + recordType);
			}
		}
		
		this.objects = objects;
		return resolve(objects.get(0));
	}
	
	public final int fb(byte b) {
		return (int)b & 0xFF;
	}
	
	
	private Object readData(int data, ByteBuffer raf) throws IOException {
        int count = readCount(data, raf);
        byte[] bytes = new byte[count];
        raf.get(bytes);
        return bytes;    
    }

	private Object readReal(int data, ByteBuffer raf) throws IOException {
		int count = 1 << (data & 0xf);
        switch (count) {
        case 4 :
            return raf.getFloat();
        case 8 :
            return raf.getDouble();
        default :
            throw new IOException("parseReal: unsupported byte count:"+count);
        }
    }

	private Object readInteger(int data, ByteBuffer raf) throws IOException {
		int byteCount = 1 << (data & 0xf);
		int dest = 0;
		for (int i = 0; i < byteCount; i++) {
			int b = fb(raf.get());
			dest = (dest << 8) | b;
		}

		return dest;
	}

	private String readUnicodeString(int data, ByteBuffer raf) throws IOException {
		int count = readCount(data, raf);
		
		char[] chars = new char[count];
		for (int i = 0; i < count; i++) {
			chars[i] = raf.getChar();
		}
		return new String(chars);
	}

	private Object readArray(int data, ByteBuffer raf, final boolean readshort) throws IOException {
		int count = readCount(data, raf); 
		
		Ref[] dest = new Ref[count];
		for (int i = 0; i < count; i++) {
			if (readshort) {
				dest[i] = new Ref(raf.getShort());
			} else {
				dest[i] = new Ref(fb(raf.get()));
			}
		}
		
		return new RefCollection(RefCollection.ARRAY, dest);
	}

	private int readCount(int data, ByteBuffer raf) throws IOException {
		int count = (data & 0xF); 
		if (count == 15) {
			int m = fb(raf.get());
			int byteCount = 1 << (m & 0xf);
			count = 0;
			for (int i = 0; i < byteCount; i++) {
				int b = fb(raf.get());
				count = (count << 8) | b;
			}
		}
		return count;
	}

	private Object readSimple(int data, Buffer raf) {
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
		List<Object> dest = new ArrayList<Object>(rc.getRefs().length);
		for (Ref ref : rc.getRefs()) {
			dest.add(resolve(ref.resolve()));
		}
		return dest;
	}

	private Map<Object, Object> resolveMap(RefCollection rc) {
		int refCount = rc.getRefs().length / 2;
		Map<Object, Object> dest = new LinkedHashMap<Object, Object>(refCount);
		
		Ref[] refs = rc.getRefs();
		for (int i = 0; i < refCount; i++) {
			Ref key = refs[i * 2];
			Ref value = refs[(i * 2) + 1];
			dest.put(resolve(key.resolve()), resolve(value.resolve()));
		}
		
		return dest;
	}
	
	private String readString(int data, ByteBuffer raf) throws IOException {
		int count = readCount(data, raf);

		byte[] bytes = new byte[count];
		raf.get(bytes);
		return new String(bytes);
	}

	private Object readDictionary(int data, ByteBuffer raf, final boolean ints) throws IOException {
		int count = readCount(data, raf); 
		
		Ref[] refs = new Ref[count * 2];
		for (int i = 0; i < count; i++) {
			if (ints) {
				refs[i * 2] = new Ref(raf.getShort());
			} else {
				refs[i * 2] = new Ref(fb(raf.get()));
			}
		}
		
		for (int i = 0; i < count; i++) {
			if (ints) {
				refs[(i * 2) + 1] = new Ref(raf.getShort());
			} else {
				refs[(i * 2) + 1] = new Ref(fb(raf.get()));
			}
		}
		
		return new RefCollection(RefCollection.DICTIONARY, refs);
	}

	private String readAsciiString(ByteBuffer buf, int offset, int length) throws IOException {
		byte[] bytes = new byte[length];
		buf.position(offset);
		buf.get(bytes);
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
	
	final class RefCollection {
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
