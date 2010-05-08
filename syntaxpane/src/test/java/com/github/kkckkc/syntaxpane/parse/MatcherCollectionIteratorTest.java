package com.github.kkckkc.syntaxpane.parse;

import junit.framework.TestCase;
import kkckkc.syntaxpane.parse.MatcherCollectionIterator;
import kkckkc.syntaxpane.regex.Matcher;
import kkckkc.syntaxpane.regex.NamedPatternFactory;

import com.google.code.regex.NamedMatcher;
import com.google.code.regex.NamedPattern;

public class MatcherCollectionIteratorTest extends TestCase {
	public void testWithEmptyMatchers() {
		MatcherCollectionIterator iterator = new MatcherCollectionIterator(new Matcher[] {});
		assertFalse(iterator.hasNext());
	}

	public void testWithNonMatching() {
		String s = "abcd1234";
		MatcherCollectionIterator iterator = new MatcherCollectionIterator(new Matcher[] {
				new NamedPatternFactory().create("kkckkc").matcher(s),
				new NamedPatternFactory().create("zzzzz").matcher(s),
		});
		assertFalse(iterator.hasNext());
	}

	public void testWithMatchingAtEnd() {
		String s = "abcd1234";
		MatcherCollectionIterator iterator = new MatcherCollectionIterator(new Matcher[] {
				new NamedPatternFactory().create("kkckkc").matcher(s),
				new NamedPatternFactory().create("1234").matcher(s),
		});
		assertTrue(iterator.hasNext());
		
		int i = iterator.next();
		assertFalse(iterator.hasNext());
		
		assertEquals(1, i);
	}
}
