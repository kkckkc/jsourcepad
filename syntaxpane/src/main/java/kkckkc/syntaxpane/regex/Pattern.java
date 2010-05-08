package kkckkc.syntaxpane.regex;

public interface Pattern {
	public Matcher matcher(CharSequence cs);

	public String pattern();
}
