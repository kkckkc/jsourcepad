package kkckkc.utils;

public class EnvironmentUtils {
	public static boolean isMac() {
		String vers = System.getProperty("os.name").toLowerCase();
		return (vers.indexOf("mac") != -1);
	}
}