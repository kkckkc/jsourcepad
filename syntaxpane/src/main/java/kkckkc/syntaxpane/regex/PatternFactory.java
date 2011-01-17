package kkckkc.syntaxpane.regex;

public interface PatternFactory {

    public static final int CASE_INSENSITIVE = 1;

	public abstract Pattern create(String s);
    public abstract Pattern create(String s, int options);
}