package kkckkc.utils;

public class Os {
	public static boolean isMac() {
		String vers = System.getProperty("os.name").toLowerCase();
		return (vers.indexOf("mac") != -1);
	}

    public static boolean isLinux() {
        String vers = System.getProperty("os.name").toLowerCase();
        return (vers.indexOf("linux") != -1);
    }

    public static boolean isWindows() {
        String vers = System.getProperty("os.name").toLowerCase();
        return (vers.indexOf("windows") != -1);
    }
}
