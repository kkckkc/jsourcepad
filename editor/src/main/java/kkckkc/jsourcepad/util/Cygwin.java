package kkckkc.jsourcepad.util;

public class Cygwin {
    public static String makePathForDirectUsage(String path) {
        return path.replace('\\', '/').replaceAll("^([a-zA-Z]):", "/cygdrive/$1").replaceAll(" ", "\\\\ ");
    }

    public static String makePathForEnvironmentUsage(String path) {
        return path.replace('\\', '/').replaceAll("^([a-zA-Z]):", "/cygdrive/$1");
    }

    public static String toFile(String path) {
        return path.replaceAll("^/cygdrive/([A-Za-z])", "$1:");
    }
}
