package kkckkc.syntaxpane.util;

public class StringUtils {
	public static String replace(String s, String pattern, String replacement) {
		if (s.indexOf(pattern) < 0) return s;
		
		final StringBuilder dest = new StringBuilder(s.length() + replacement.length() * 10);

		int from = 0;
		int to = 0;
		while ((to = s.indexOf(pattern, from)) >= 0) {
			dest.append(s.substring(from, to));
			dest.append(replacement);
			from = to + pattern.length();
		}
		dest.append(s.substring(from));
		return dest.toString();
	}

	public static String capitalize(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
}
