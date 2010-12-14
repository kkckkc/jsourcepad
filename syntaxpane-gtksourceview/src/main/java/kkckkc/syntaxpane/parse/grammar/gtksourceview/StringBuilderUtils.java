package kkckkc.syntaxpane.parse.grammar.gtksourceview;

public class StringBuilderUtils {
	public static void replace(StringBuilder builder, String pattern, String replacement) {
		int len = pattern.length();
		if (replacement.length() > len) {
			builder.ensureCapacity(builder.length() + 5 * replacement.length());
		}
		int pos = 0;
		while ((pos = builder.indexOf(pattern, pos)) >= 0) {
			builder.replace(pos, pos + len, replacement);
			pos = pos + len;
		}
	}
}
