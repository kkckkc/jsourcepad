package kkckkc.jsourcepad.util;

public interface Null {
    public static class Utils {
        public static boolean isNull(Object o) {
            return o == null || o instanceof Null;
        }

        public static boolean isNotNull(Object o) {
            return ! isNull(o);
        }
    }
}
