package kkckkc.jsourcepad.util;

public class Cygwin {
    public static String makePath(String path) {
        return path.replace('\\', '/').replaceAll("^([A-Z]):", "/cygdrive/$1").replaceAll(" ", "\\\\ ");
    }
}
