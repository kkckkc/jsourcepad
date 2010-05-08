package com.github.kkckkc.syntaxpane;


import java.io.File;

import kkckkc.syntaxpane.parse.grammar.Language;
import kkckkc.syntaxpane.parse.grammar.LanguageManager;
import kkckkc.syntaxpane.parse.grammar.gtksourceview.GtkSourceViewLanguageProvider;



public class ParseAllTest {
	public static void main(String...strings) {
		for (int i = 0; i < 10; i++) {
		long s = System.currentTimeMillis();
		
		LanguageManager languageManager = new LanguageManager();
		languageManager.setProvider(new GtkSourceViewLanguageProvider(
				new File("/usr/share/gtksourceview-2.0/language-specs")));
		
		for (Language l : languageManager.getLanguages()) {
//			if (l.getLanguageId().equals("scheme")) continue;

//			System.out.println(l.getLanguageId());
			l.compile();
		}
		
		System.out.println(System.currentTimeMillis() - s);
		}
	}
}
