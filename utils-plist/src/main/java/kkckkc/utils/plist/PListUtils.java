package kkckkc.utils.plist;

import java.util.List;
import java.util.Map;

public class PListUtils {

	@SuppressWarnings("unchecked")
    public static <T> T get(Object o, Class<? extends T> type, Object... keys) {
		for (Object key : keys) {
			o = get(o, key);
		}
		return (T) o;
	}

	@SuppressWarnings("unchecked")
    private static Object get(Object o, Object key) {
		if (o == null) return null;
		
		if (o instanceof Map) {
			if (! (key instanceof String)) {
//				System.out.println(o + " " + key);
				return null;
			}
			return ((Map) o).get(key);
		} else if (o instanceof List) {
			if (! (key instanceof Integer)) {
//				System.out.println(o + " " + key);
				return null;
			}
			return ((List) o).get((Integer) key);
		} else {
			return null;
		}
    }
	
}
