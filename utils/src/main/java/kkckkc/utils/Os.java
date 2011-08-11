package kkckkc.utils;

public class Os {
    private static final String OS_IDENTIFIER = System.getProperty("os.name").toLowerCase();

	public static boolean isMac() {
        return OS_IDENTIFIER.contains("mac");
	}

    public static boolean isLinux() {
        return OS_IDENTIFIER.contains("linux");
    }

    public static boolean isWindows() {
        return OS_IDENTIFIER.contains("windows");
    }
}
