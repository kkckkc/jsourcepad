package kkckkc.syntaxpane.regex;

import junit.framework.TestCase;

public abstract class AbstractPatternFactoryTest extends TestCase {

    protected abstract PatternFactory getPatternFactory();

    public void testPatternPattern() {
        PatternFactory patternFactory = getPatternFactory();
        Pattern pattern = patternFactory.create("test");
        assertEquals("test", pattern.pattern());
    }

    public void testMatcherMatches() {
        PatternFactory patternFactory = getPatternFactory();
        Pattern pattern = patternFactory.create("test");

        Matcher matcher;

        matcher = pattern.matcher("test");
        assertTrue(matcher.matches());

        matcher = pattern.matcher("kalletestolletest");
        assertTrue(matcher.matches());

        matcher = pattern.matcher("olle");
        assertFalse(matcher.matches());
    }

    public void testMatcherMatchesAll() {
        PatternFactory patternFactory = getPatternFactory();
        Pattern pattern = patternFactory.create("test");

        Matcher matcher;

        matcher = pattern.matcher("test");
        assertTrue(matcher.matchesAll());

        matcher = pattern.matcher("kalletestolletest");
        assertFalse(matcher.matchesAll());

        matcher = pattern.matcher("olle");
        assertFalse(matcher.matches());
    }

    // TODO: matches() and find() are the same methods
    public void testMatcherFind() {
        PatternFactory patternFactory = getPatternFactory();
        Pattern pattern = patternFactory.create("test");

        Matcher matcher;

        matcher = pattern.matcher("test");
        assertTrue(matcher.find());

        matcher = pattern.matcher("kalletestolletest");
        assertTrue(matcher.find());

        matcher = pattern.matcher("olle");
        assertFalse(matcher.find());
    }

    public void testMatcherFindMultipleTimes() {
        PatternFactory patternFactory = getPatternFactory();
        Pattern pattern = patternFactory.create("test");

        Matcher matcher;

        matcher = pattern.matcher("test");
        assertTrue(matcher.find());
        assertTrue(matcher.find());

        matcher = pattern.matcher("kalletestolletest");
        assertTrue(matcher.find());
        assertTrue(matcher.find());

        matcher = pattern.matcher("olle");
        assertFalse(matcher.find());
        assertFalse(matcher.find());
    }

    public void testMatcherReplace() {
        PatternFactory patternFactory = getPatternFactory();
        Pattern pattern = patternFactory.create("test");

        Matcher matcher;

        matcher = pattern.matcher("test");
        assertEquals("kalle", matcher.replacementString("kalle"));

        matcher = pattern.matcher("kalletestolletest");
        assertEquals("2", matcher.replacementString("2"));

        matcher = pattern.matcher("olle");
        assertEquals("2", matcher.replacementString("2"));
    }

    public void testMatcherReplaceAll() {
        PatternFactory patternFactory = getPatternFactory();
        Pattern pattern = patternFactory.create("test");

        Matcher matcher;

        matcher = pattern.matcher("test");
        assertEquals("kalle", matcher.replaceAll("kalle"));

        matcher = pattern.matcher("kalletestolletest");
        assertEquals("kalle2olle2", matcher.replaceAll("2"));

        matcher = pattern.matcher("olle");
        assertEquals("olle", matcher.replaceAll("2"));
    }

}
