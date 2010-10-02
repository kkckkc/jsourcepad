package kkckkc.syntaxpane.parse.grammar.gtksourceview;

public class StringBuilderUtils {
	public static void replace(StringBuilder b, String pattern, String replacement) {
		int len = pattern.length();
		if (replacement.length() > len) {
			b.ensureCapacity(b.length() + 5 * replacement.length());
		}
		int pos = 0;
		while ((pos = b.indexOf(pattern, pos)) >= 0) {
			b.replace(pos, pos + len, replacement);
			pos = pos + len;
		}
		return;
	}
}
