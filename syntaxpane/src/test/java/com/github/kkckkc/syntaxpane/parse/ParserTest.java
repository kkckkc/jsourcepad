package com.github.kkckkc.syntaxpane.parse;

import static kkckkc.syntaxpane.parse.grammar.builder.ContextBuilder.containerContext;
import static kkckkc.syntaxpane.parse.grammar.builder.ContextBuilder.referenceContext;
import static kkckkc.syntaxpane.parse.grammar.builder.ContextBuilder.rootContext;
import static kkckkc.syntaxpane.parse.grammar.builder.ContextBuilder.simpleContext;

import java.io.IOException;


import junit.framework.TestCase;
import kkckkc.syntaxpane.model.FoldManager;
import kkckkc.syntaxpane.model.LineManager;
import kkckkc.syntaxpane.parse.CharProvider;
import kkckkc.syntaxpane.parse.Parser;
import kkckkc.syntaxpane.parse.Parser.ChangeEvent;
import kkckkc.syntaxpane.parse.grammar.Language;
import kkckkc.syntaxpane.parse.grammar.LanguageManager;
import kkckkc.syntaxpane.regex.NamedPatternFactory;
import kkckkc.syntaxpane.regex.Pattern;

public class ParserTest extends TestCase {
	
	public static final String BASE = "src/test/resources/syntaxpane/parse/testcases/";

	private Pattern re(String string) {
	    return new NamedPatternFactory().create(string);
    }
	
	public void testParser1() throws IOException {
		String[][] ids = { 
				{ "input1-1.txt", "expected1-1.txt" },
				{ "input1-2.txt", "expected1-2.txt" },
		};
		
		Language l = new Language("test");
		l.setRootContext(rootContext("root").childRefs( 
				containerContext("string").begin(re("\"")).end(re("\"")).build(),
				containerContext("comment").begin(re("/\\*")).end(re("\\*/")).build()
			).build());
		l.setLanguageManager(new LanguageManager());
		l.compile();

		test(l, "parser1", ids);
	}

	public void testParser2() throws IOException {
		String[][] ids = { 
				{ "input1-2.txt", "expected1-2.txt" },
		};
		
		Language l = new Language("test");
		l.setRootContext(rootContext("root").childRefs( 
					containerContext("string").begin(re("\"")).end(re("\"")).build(),
					containerContext("comment").begin(re("/\\*")).end(re("\\*/")).
						childRefs(referenceContext("comment").build()).
					build()
			).build());
		l.setLanguageManager(new LanguageManager());
		l.compile();

		test(l, "parser2", ids);
	}

	private void test(Language l, String parser, String[][] ids) throws IOException {
		for (String[] f : ids) {
			String input = FileUtils.readFile(BASE + parser + "/" + f[0]);
			CharProvider pro = new CharProvider.StringBuffer(input);
			LineManager lineManager = new LineManager(pro);
			Parser p = new Parser(l, lineManager, new FoldManager(lineManager));
			p.parse(0, pro.getLength(), ChangeEvent.ADD);
			
			StringBuffer b = new StringBuffer();
			lineManager.dumpXml(b);

			assertEquals(
					"Incorrect parsing of " + f[0] + " using " + parser, 
					FileUtils.readFile(BASE + parser + "/" + f[1]),
					b.toString()
			);
		}
	}

	public void testIncrementalSameLine() {
		Language l = new Language("test");
		l.setRootContext(rootContext("root").childRefs( 
				containerContext("string").begin(re("\"")).end(re("\"")).build(),
				containerContext("comment").begin(re("/\\*")).end(re("\\*/")).
					childRefs(referenceContext("comment").build()).
				build()
			).build());
		l.setLanguageManager(new LanguageManager());
		l.compile();

		StringBuffer b = new StringBuffer("Lorem ipsum \"dolor");
	
		CharProvider pro = new CharProvider.StringBuffer(b); 
		LineManager lineManager = new LineManager(pro);
		Parser p = new Parser(l, lineManager, new FoldManager(lineManager));

		p.parse(0, pro.getLength(), ChangeEvent.ADD);

		int len = pro.getLength();
		
		b.append("\" sit amet");
		p.parse(len, pro.getLength(), ChangeEvent.ADD);
		
		assertEquals("<root>Lorem ipsum <string>\"dolor\"</string> sit amet</root>\n", 
				lineManager.dumpXml());
	}

	public void testIncrementalNewLine() {
		Language l = new Language("test");
		l.setRootContext(rootContext("root").childRefs(
				containerContext("string").begin(re("\"")).end(re("\"")).build(),
				containerContext("comment").begin(re("/\\*")).end(re("\\*/")).
					childRefs(referenceContext("comment").build()).
				build()
			).build());
		l.setLanguageManager(new LanguageManager());
		l.compile();


		StringBuffer b = new StringBuffer("Lorem ipsum \"dolor\n");
	
		CharProvider pro = new CharProvider.StringBuffer(b); 
		LineManager lineManager = new LineManager(pro);
		Parser p = new Parser(l, lineManager, new FoldManager(lineManager));

		p.parse(0, pro.getLength(), ChangeEvent.ADD);
		int len = pro.getLength();
		
		b.append("\" sit amet");
		p.parse(len, pro.getLength(), ChangeEvent.ADD);
		
		assertEquals(
				"<root>Lorem ipsum <string>\"dolor</string></root>\n" + 
				"<root><string>\"</string> sit amet</root>\n", 
				lineManager.dumpXml());
	}

	public void testIncrementalNewLine2() {
		Language l = new Language("test");
		l.setRootContext(rootContext("root").childRefs( 
				containerContext("string").begin(re("\"")).end(re("\"")).build(),
				containerContext("comment").begin(re("/\\*")).end(re("\\*/")).
					childRefs(referenceContext("comment").build()).
					build()
			).build());
		l.setLanguageManager(new LanguageManager());
		l.compile();


		StringBuffer b = new StringBuffer("Lorem ipsum \"dolor");
	
		CharProvider pro = new CharProvider.StringBuffer(b); 
		LineManager lineManager = new LineManager(pro);
		Parser p = new Parser(l, lineManager, new FoldManager(lineManager));

		p.parse(0, pro.getLength(), ChangeEvent.ADD);
		int len = pro.getLength();

		b.append("\n");
		p.parse(len, pro.getLength(), ChangeEvent.ADD);

		len = pro.getLength();
		
		b.append("\" sit amet");
		p.parse(len, pro.getLength(), ChangeEvent.ADD);
		
		assertEquals(
				"<root>Lorem ipsum <string>\"dolor</string></root>\n" + 
				"<root><string>\"</string> sit amet</root>\n", 
				lineManager.dumpXml());
	}
	

	public void testRemoveSameLine() {
		Language l = new Language("test");
		l.setRootContext(rootContext("root").childRefs( 
				containerContext("string").begin(re("\"")).end(re("\"")).build(),
				containerContext("comment").begin(re("/\\*")).end(re("\\*/")).
					childRefs(referenceContext("comment").build()).
					build()
			).build());
		l.setLanguageManager(new LanguageManager());
		l.compile();

		StringBuffer b = new StringBuffer("Lorem ipsum \"dolor\" sit amet");
	
		CharProvider pro = new CharProvider.StringBuffer(b); 
		LineManager lineManager = new LineManager(pro);
		Parser p = new Parser(l, lineManager, new FoldManager(lineManager));

		p.parse(0, pro.getLength(), ChangeEvent.ADD);

		b.delete(16, 19);
		
		p.parse(16, 19, ChangeEvent.REMOVE);
		
		assertEquals("<root>Lorem ipsum <string>\"dol sit amet</string></root>\n", 
				lineManager.dumpXml());
	}
	
	public void testDeleteLineBreak() {
		Language l = new Language("test");
		l.setRootContext(rootContext("root").childRefs( 
				containerContext("string").begin(re("\"")).end(re("\"")).build(),
				simpleContext("keyword").match(re("public|private")).build(),
				containerContext("comment").begin(re("/\\*")).end(re("\\*/")).
					childRefs(referenceContext("comment").build()).
					build()
			).build());
		l.setLanguageManager(new LanguageManager());
		l.compile();
		
		StringBuffer b = new StringBuffer("pub\nlic");
		
		CharProvider pro = new CharProvider.StringBuffer(b); 
		LineManager lineManager = new LineManager(pro);
		Parser p = new Parser(l, lineManager, new FoldManager(lineManager));

		p.parse(0, pro.getLength(), ChangeEvent.ADD);

		b.delete(3, 4);
		
		p.parse(3, 4, ChangeEvent.REMOVE);
		
		assertEquals("<root><keyword>public</keyword></root>\n", 
				lineManager.dumpXml());
	}
	
	public void testDeleteComment() {
		Language l = new Language("test");
		l.setRootContext(rootContext("root").childRefs(
				containerContext("string").begin(re("\"")).end(re("\"")).build(),
				simpleContext("keyword").match(re("public|private")).build(),
				containerContext("comment").begin(re("/\\*")).end(re("\\*/")).
					childRefs(referenceContext("comment").build()).
					build()
			).build());
		l.setLanguageManager(new LanguageManager());
		l.compile();
		
		StringBuffer b = new StringBuffer("/* Lorem\nIpsum\nDolor */");
		
		CharProvider pro = new CharProvider.StringBuffer(b); 
		LineManager lineManager = new LineManager(pro);
		Parser p = new Parser(l, lineManager, new FoldManager(lineManager));

		p.parse(0, pro.getLength(), ChangeEvent.ADD);

		b.delete(1, 2);
		
		p.parse(1, 2, ChangeEvent.REMOVE);
		
		System.out.println(lineManager.dumpXml());
		assertEquals(
				"<root>/ Lorem</root>\n" +
				"<root>Ipsum</root>\n" + 
				"<root>Dolor */</root>\n", 
				lineManager.dumpXml());
	}
	
	public void testFirstCharComment() {
		Language l = new Language("test");
		l.setRootContext(rootContext("root").childRefs( 
				containerContext("string").begin(re("\"")).end(re("\"")).build(),
				simpleContext("keyword").match(re("public|private")).build(),
				containerContext("comment").begin(re("/\\*")).end(re("\\*/")).
					childRefs(referenceContext("comment").build()).
					build()
			).build());
		l.setLanguageManager(new LanguageManager());
		l.compile();
		
		StringBuffer b = new StringBuffer("x");
		
		CharProvider pro = new CharProvider.StringBuffer(b); 
		LineManager lineManager = new LineManager(pro);
		Parser p = new Parser(l, lineManager, new FoldManager(lineManager));

		p.parse(0, pro.getLength(), ChangeEvent.ADD);

		b.delete(0, 1);
		
		p.parse(0, 1, ChangeEvent.REMOVE);
		
		System.out.println(lineManager.dumpXml());
		assertEquals(
				"<root></root>\n",
				lineManager.dumpXml());
	}
}
