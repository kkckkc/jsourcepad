import kkckkc.syntaxpane.regex.JoniPatternFactory;
import kkckkc.syntaxpane.regex.Matcher;
import kkckkc.syntaxpane.regex.Pattern;
import kkckkc.syntaxpane.regex.PatternFactory;


public class JoniPatternTest {
	public static final void main(String... strings) {
		PatternFactory pf = new JoniPatternFactory();
		Pattern p = pf.create("(?={|implements)");
		Matcher m = p.matcher("public class A implements B {");
		System.out.println(m.find());
		System.out.println(m.start());
		System.out.println(m.end());
	}
}
