package kkckkc.syntaxpane.parse.grammar.gtksourceview;

import kkckkc.syntaxpane.regex.NamedPatternFactory;
import kkckkc.syntaxpane.regex.Pattern;

import java.util.Map;

public class RegexpUtils {
	private static java.util.regex.Pattern LEFT_BRACE = java.util.regex.Pattern.compile("(?<!\\\\)\\{");
	private static java.util.regex.Pattern RIGHT_BRACE = java.util.regex.Pattern.compile("(?<!\\\\)\\}");
	
	public static String encode(String str, Map<String, String> replacements) {
		StringBuilder builder = new StringBuilder(str.length() * 2);
		builder.append(str);
		
		if (replacements != null && str.indexOf('%') >= 0) {
			for (Map.Entry<String, String> entry : replacements.entrySet()) {
				StringBuilderUtils.replace(builder, "\\%{" + entry.getKey() + "}", entry.getValue());
			}
		}
		
		// Don't know what these expressions are for
		StringBuilderUtils.replace(builder, "\\%[", "");
		StringBuilderUtils.replace(builder, "\\%]", "");

		// Special character classes
		StringBuilderUtils.replace(builder, "[][]", "[\\]\\[]");
		StringBuilderUtils.replace(builder, "[[]", "[\\[]");

		// Remove any \x and \o as these are special unicode escapes
		StringBuilderUtils.replace(builder, "\\x", "\\\\x");
		StringBuilderUtils.replace(builder, "\\o", "\\\\o");
		
		// TODO: This is a rather ugly workaround
		StringBuilderUtils.replace(builder, "\\\\\\o", "\\\\o");
		StringBuilderUtils.replace(builder, "\\\\\\x", "\\\\x");
		
		// Encode unescaped { and } not preceeded by \
		str = LEFT_BRACE.matcher(builder).replaceAll("\\\\{");
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
