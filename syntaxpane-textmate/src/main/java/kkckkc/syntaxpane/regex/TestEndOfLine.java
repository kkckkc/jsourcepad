package kkckkc.syntaxpane.regex;

public class TestEndOfLine {

    public static void main(String... args) {
        PatternFactory pf = new JoniPatternFactory();

        System.out.println(pf.create(".*$").matcher("Hello\n").matchesAll());

        Matcher m = pf.create(".*").matcher("Hello\n");
        m.find();
        System.out.println(m.end());

        m = pf.create(".*$\n?").matcher("Hello\n");
        m.find();
        System.out.println(m.end());
    }

}
