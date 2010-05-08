package kkckkc.syntaxpane.parse.grammar.gtksourceview;

import java.util.Map;

import kkckkc.syntaxpane.regex.NamedPatternFactory;
import kkckkc.syntaxpane.regex.Pattern;
import kkckkc.syntaxpane.util.StringBuilderUtils;

public class RegexpUtils {
	private static java.util.regex.Pattern LEFT_BRACE = java.util.regex.Pattern.compile("(?<!\\\\)\\{");
	private static java.util.regex.Pattern RIGHT_BRACE = java.util.regex.Pattern.compile("(?<!\\\\)\\}");
	
	public static String encode(String str, Map<String, String> replacements) {
		StringBuilder b = new StringBuilder(str.length() * 2);
		b.append(str);
		
		if (replacements != null && str.indexOf('%') >= 0) {
			for (Map.Entry<String, String> entry : replacements.entrySet()) {
				StringBuilderUtils.replace(b, "\\%{" + entry.getKey() + "}", entry.getValue());
			}
		}
		
		// Don't know what these expressions are for
		StringBuilderUtils.replace(b, "\\%[", "");
		StringBuilderUtils.replace(b, "\\%]", "");

		// Special character classes
		StringBuilderUtils.replace(b, "[][]", "[\\]\\[]");
		StringBuilderUtils.replace(b, "[[]", "[\\[]");

		// Remove any \x and \o as these are special unicode escapes
		StringBuilderUtils.replace(b, "\\x", "\\\\x");
		StringBuilderUtils.replace(b, "\\o", "\\\\o");
		
		// TODO: This is a rather ugly workaround
		StringBuilderUtils.replace(b, "\\\\\\o", "\\\\o");
		StringBuilderUtils.replace(b, "\\\\\\x", "\\\\x");
		
		// Encode unescaped { and } not preceeded by \
		str = LEFT_BRACE.matcher(b).replaceAll("\\\\{");
		str = RIGHT_BRACE.matcher(str).replaceAll("\\\\}");

		// Special handling of "single" backslashes
		if (str.equals("\\")) str = "\\\\";
		else if (str.equals("\\\\")) { }
		else if (str.endsWith("\\")) str = str + "\\";
		
		return str;
	}
	
	public static Pattern parse(String str, Map<String, String> replacements) {
		return new NamedPatternFactory().create(encode(str, replacements));
	}
}
