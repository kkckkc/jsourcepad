package kkckkc.syntaxpane.regex;

import org.joni.Option;
import org.joni.Regex;

public class Test {
	public static void main(String... args) {
		char[] c = "([a-z]([0-9]+))".toCharArray();
		
		Regex re = new Regex(c, 0, c.length, Option.DEFAULT, null);
		
		char[] ma = "__a892   gh789".toCharArray();
		
		org.joni.Matcher m = re.matcher(ma);
		
		System.out.println(m.search(0, ma.length, Option.NONE));
		System.out.println(m.getRegion().numRegs);
		System.out.printf("%d - %d\n", m.getRegion().beg[0], m.getRegion().end[0]);
		System.out.printf("%d - %d\n", m.getRegion().beg[1], m.getRegion().end[1]);
		System.out.printf("%d - %d\n", m.getRegion().beg[2], m.getRegion().end[2]);

		System.out.println(m.search(6, ma.length, Option.NONE));
		System.out.println(m.getRegion().numRegs);
		System.out.printf("%d - %d\n", m.getRegion().beg[0], m.getRegion().end[0]);
		System.out.printf("%d - %d\n", m.getRegion().beg[1], m.getRegion().end[1]);
		System.out.printf("%d - %d\n", m.getRegion().beg[2], m.getRegion().end[2]);
		
	}
}
