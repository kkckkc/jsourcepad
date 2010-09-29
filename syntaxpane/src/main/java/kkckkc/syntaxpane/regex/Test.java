package kkckkc.syntaxpane.regex;

public class Test {
	public static void main(String... args) {
		JoniPatternFactory jpf = new JoniPatternFactory();
        Pattern p = jpf.create("abc[a-z]*ef");
        Matcher m = p.matcher("1234abcef56abcdffgef6");
        System.out.println(m.replaceAll("kalle"));
	}
}
