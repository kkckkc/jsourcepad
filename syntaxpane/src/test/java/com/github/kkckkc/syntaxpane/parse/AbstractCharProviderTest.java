package com.github.kkckkc.syntaxpane.parse;


import junit.framework.TestCase;
import kkckkc.syntaxpane.parse.CharProvider;

public class AbstractCharProviderTest extends TestCase {
	public void testFindNotFound() {
		StringBuffer b = new StringBuffer();
		b.append(seq(300, '*'));
		assertEquals(-1, new CharProvider.StringBuffer(b.toString()).find(0, '-'));
	}

	public void testFindNotFoundShortString() {
		StringBuffer b = new StringBuffer();
		b.append(seq(3, '*'));
		assertEquals(-1, new CharProvider.StringBuffer(b.toString()).find(0, '-'));
	}

	public void testFindNotFoundEmptyString() {
		StringBuffer b = new StringBuffer();
		assertEquals(-1, new CharProvider.StringBuffer(b.toString()).find(0, '-'));
	}

	public void testFindFirst() {
		StringBuffer b = new StringBuffer();
		b.append("-").append(seq(3, '*'));
		assertEquals(0, new CharProvider.StringBuffer(b.toString()).find(0, '-'));
	}

	public void testFindLast() {
		StringBuffer b = new StringBuffer();
		b.append(seq(3, '*')).append("-");
		assertEquals(3, new CharProvider.StringBuffer(b.toString()).find(0, '-'));
	}
	
	StringBuffer seq(int count, char c) {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < count; i++) {
			b.append(c);
		}
		return b;
	}
}
