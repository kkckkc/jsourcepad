package kkckkc.jsourcepad.util;

import java.io.File;

public class ApplicationFolder {
	
	public static File get() {
		return new File(System.getProperty("user.home"), ".jsourcepad");
	}
	
	public static File get(String s) {
		return new File(get(), s);
	}
}
