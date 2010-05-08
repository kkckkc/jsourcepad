package kkckkc.syntaxpane.util;

public class CharSequenceUtils {
	public static boolean startsWith(CharSequence a, CharSequence b) {
		int len = b.length();
		if (a.length() < len) return false;
		
		for (int i = 0; i < len; i++) {
			if (a.charAt(i) != b.charAt(i) || i >= a.length()) {
				return false;
			}
		}
		return true;
	}
}
