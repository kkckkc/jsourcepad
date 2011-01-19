package kkckkc.utils;

public class StringUtils {

    public static String replace(String s, String pattern, String replacement) {
		if (s.indexOf(pattern) < 0) return s;
		
		final StringBuilder builder = new StringBuilder(s.length() + replacement.length() * 10);

		int from = 0;
		int to;
		while ((to = s.indexOf(pattern, from)) >= 0) {
			builder.append(s.substring(from, to));
			builder.append(replacement);
			from = to + pattern.length();
		}
		builder.append(s.substring(from));
		return builder.toString();
	}

    public static String stripPrefix(String s, String prefix) {
        if (s.startsWith(prefix)) {
            return s.substring(prefix.length());
        }
        return s;
    }

    public static String beforeFirst(String content, String delimiter) {
        int idx = content.indexOf(delimiter);
        if (idx == -1) return content;
        return content.substring(0, idx);
    }
}
