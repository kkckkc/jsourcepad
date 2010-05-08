package kkckkc.syntaxpane.util;

public class StringBuilderUtils {
	public static void replace(StringBuilder b, String pattern, String replacement) {
		int len = pattern.length();
		b.ensureCapacity(b.length() + 5 * len);
		int pos = 0;
		while ((pos = b.indexOf(pattern, pos)) >= 0) {
			b.replace(pos, pos + len, replacement);
			pos = pos + len;
		}
		return;
	}
}
