package com.github.kkckkc.syntaxpane.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Performance {
	private static final String test = "Lorem ipsum 'dolor' sit 'amet' 'consectetuer'";
	private static final Pattern p = Pattern.compile("'");
	
	private static int i = 0;
	
	public static void main(String... args) {
		for (int i = 0; i < 30000000; i++) {
			testRegexp();
			testIndexOf();
		}
		
		long start = System.currentTimeMillis();
		for (int i = 0; i < 30000000; i++) {
			testRegexp();
		}
		System.out.println("Regexp " + (System.currentTimeMillis() - start));
		
		start = System.currentTimeMillis();
		for (int i = 0; i < 30000000; i++) {
			testIndexOf();
		}
		System.out.println("Indexof " + (System.currentTimeMillis() - start));
	}

	private static void testIndexOf() {
		int idx = 0;
		while ((idx = test.indexOf("'", idx) + 1) > 0) {
			i++;
		}
	}

	private static void testRegexp() {
		Matcher m = p.matcher(test);
		int idx = 0;
		while (m.find(idx)) {
			idx = m.start() + 1;
			i++;
		}
	}
}
