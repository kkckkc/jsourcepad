package kkckkc.jsourcepad.model.bundle.snippet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SnippetUtils {
	public static final Pattern LAST_WORD_PATTERN = Pattern.compile("(^|\\s+|.*\n)(\\S+)(\\s*)$", Pattern.DOTALL);
	
	public static String getSnippet(String s) {
		Matcher matcher = LAST_WORD_PATTERN.matcher(s);
		if (! matcher.matches()) return "";
		else return matcher.group(2).trim();
	}
}
