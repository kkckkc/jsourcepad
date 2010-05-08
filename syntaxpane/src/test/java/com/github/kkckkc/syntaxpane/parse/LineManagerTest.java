package com.github.kkckkc.syntaxpane.parse;

import java.util.Iterator;



import junit.framework.TestCase;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.syntaxpane.model.LineManager;
import kkckkc.syntaxpane.model.LineManager.Line;
import kkckkc.syntaxpane.parse.CharProvider;
 
public class LineManagerTest extends TestCase {
	public void testAddOneLine() {
		StringBuffer b = new StringBuffer("Lorem ipsum");
		CharProvider p = new CharProvider.StringBuffer(b);
		
		LineManager lineManager = new LineManager(p);
		lineManager.addInterval(new Interval(0, b.length()));
		
		assertState(lineManager, new Interval(0, 11));
	}

	public void testAddTwoLine() {
		StringBuffer b = new StringBuffer("Lorem ipsum\ndolor sit amet");
		CharProvider p = new CharProvider.StringBuffer(b);
		
		LineManager lineManager = new LineManager(p);
		lineManager.addInterval(new Interval(0, b.length()));
		
		assertState(lineManager, new Interval(0, 11), new Interval(12, 26));
	}

	public void testAddLineInMiddle() {
		StringBuffer b = new StringBuffer("Lorem ipsum\ndolor sit amet");
		CharProvider p = new CharProvider.StringBuffer(b);
		
		LineManager lineManager = new LineManager(p);
		lineManager.addInterval(new Interval(0, b.length()));
		
		b.insert(12, "consectetuer\n");
		lineManager.addInterval(new Interval(12, 12 + "consectetuer\n".length()));
		
		assertState(lineManager, new Interval(0, 11), new Interval(12, 24), new Interval(25, 39));
	}

	public void testTextInLine() {
		StringBuffer b = new StringBuffer("Lorem ipsum\ndolor sit amet");
		CharProvider p = new CharProvider.StringBuffer(b);
		
		LineManager lineManager = new LineManager(p);
		lineManager.addInterval(new Interval(0, b.length()));
		
		b.insert(11, " consectetuer");
		lineManager.addInterval(new Interval(11, 11 + " consectetuer".length()));
		
		assertState(lineManager, new Interval(0, 24), new Interval(25, 39));
	}

	public void testAddLineInLine() {
		StringBuffer b = new StringBuffer("Lorem ipsum\ndolor sit amet");
		CharProvider p = new CharProvider.StringBuffer(b);
		
		LineManager lineManager = new LineManager(p);
		lineManager.addInterval(new Interval(0, b.length()));
		
		b.insert(11, " consectetuer\ntest\n");
		lineManager.addInterval(new Interval(11, 11 + " consectetuer\ntest\n".length()));
		
		assertState(lineManager, new Interval(0, 24), new Interval(25, 29), new Interval(30, 30), new Interval(31, 45));
	}

	public void testRemoveInLine() {
		StringBuffer b = new StringBuffer("Lorem ipsum dolor sit amet");
		CharProvider p = new CharProvider.StringBuffer(b);
		
		LineManager lineManager = new LineManager(p);
		lineManager.addInterval(new Interval(0, b.length()));
		
		b.delete(12, 18);
		lineManager.removeInterval(new Interval(12, 18));

		assertState(lineManager, new Interval(0, 20));
	}

	public void testRemoveCrossLine() {
		StringBuffer b = new StringBuffer("Lorem ipsum\ndolor sit amet");
		CharProvider p = new CharProvider.StringBuffer(b);
		
		LineManager lineManager = new LineManager(p);
		lineManager.addInterval(new Interval(0, b.length()));
		
		b.delete(8, 18);
		lineManager.removeInterval(new Interval(8, 18));
		
		assertState(lineManager, new Interval(0, 16));
	}
	
	public void testRemoveLine() {
		StringBuffer b = new StringBuffer("\n\n\n");
		CharProvider p = new CharProvider.StringBuffer(b);
		
		LineManager lineManager = new LineManager(p);
		lineManager.addInterval(new Interval(0, b.length()));
		
		b.delete(2, 3);
		lineManager.removeInterval(new Interval(2, 3));

		assertState(lineManager, new Interval(0, 0), new Interval(1, 1), new Interval(2, 2));
	}
	
	private void assertState(LineManager lineManager, Interval... intervals) {
		assertEquals(lineManager.size(), intervals.length);
		
		Iterator<Line> lines = lineManager.iterator();
		for (Interval i : intervals) {
			assertEquals(i, lines.next());
		}
	}
}
