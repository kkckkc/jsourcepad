package kkckkc.utils;

public class Os {
	public static boolean isMac() {
        return (getOsIdentifier().indexOf("mac") != -1);
	}

    public static boolean isLinux() {
        return (getOsIdentifier().indexOf("linux") != -1);
    }

    public static boolean isWindows() {
        return (getOsIdentifier().indexOf("windows") != -1);
    }

    private static String getOsIdentifier() {
        return System.getProperty("os.name").toLowerCase();
    }
}
