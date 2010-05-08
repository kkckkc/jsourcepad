package com.github.kkckkc.syntaxpane.util;


import junit.framework.TestCase;
import kkckkc.syntaxpane.parse.grammar.gtksourceview.RegexpUtils;

public class RegexpUtilsTest extends TestCase {
	public void testParse() {
		assertEquals("\\{[0-9][0-9:\\#\\%,./cdefgnrxtsuDTFGMY]\\}", 
				RegexpUtils.encode("{[0-9][0-9:\\#\\%,./cdefgnrxtsuDTFGMY]}", null));
		assertEquals("\\{[0-9][0-9:\\#\\%,./cdefgnrxtsuDTFGMY]\\}", 
				RegexpUtils.encode("\\{[0-9][0-9:\\#\\%,./cdefgnrxtsuDTFGMY]\\}", null));
		
		RegexpUtils.parse("\\b((?<![\\w\\d_-])abs(?![\\w\\d_-])|(?<![\\w\\d_-])integer/cc(?![\\w\\d_-]))\\b", null);
	}
}
