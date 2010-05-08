package kkckkc.syntaxpane.regex;

public interface Matcher {

	boolean matches();

	int start();

	boolean find(int position);

	boolean find();

	int end();

	int groupCount();

	String group(int i);

	int start(int subPatternIdx);

	int end(int subPatternIdx);

	int start(String subPattern);

	int end(String subPattern);

	String replaceAll(String replacement);

}
