package com.github.kkckkc.syntaxpane.parse;


import junit.framework.TestCase;
import kkckkc.syntaxpane.model.Interval;

public class IntervalTest extends TestCase {
	public void testContainsPoint() {
		Interval r = new Interval(10, 20);
		
		assertFalse(r.contains(9));
		assertFalse(r.contains(21));
		
		assertTrue(r.contains(10));
		assertTrue(r.contains(15));
		assertTrue(r.contains(20));
	}
	
	public void testContainsRegion() {
		Interval r = new Interval(10, 20);
		
		assertFalse(r.contains(new Interval(1, 2)));
		assertFalse(r.contains(new Interval(21, 22)));
		assertFalse(r.contains(new Interval(9, 14)));
		assertFalse(r.contains(new Interval(15, 25)));
		
		assertTrue(r.contains(new Interval(10, 20)));
		assertTrue(r.contains(new Interval(10, 15)));
		assertTrue(r.contains(new Interval(15, 20)));
		assertTrue(r.contains(new Interval(15, 17)));
	}
	
	public void testIntersectsRegion() {
		Interval r = new Interval(10, 20);
		
		assertFalse(r.overlaps(new Interval(1, 2)));
		assertFalse(r.overlaps(new Interval(21, 22)));

		assertTrue(r.overlaps(new Interval(9, 14)));
		assertTrue(r.overlaps(new Interval(15, 25)));
		
		assertTrue(r.overlaps(new Interval(10, 20)));
		assertTrue(r.overlaps(new Interval(10, 15)));
		assertTrue(r.overlaps(new Interval(15, 20)));
		assertTrue(r.overlaps(new Interval(15, 17)));
		
		assertTrue(r.overlaps(new Interval(1, 101)));
	}
	
	public void testIsEmpty() {
		assertFalse(new Interval(10, 20).isEmpty());
		assertTrue(new Interval(10, 10).isEmpty());
		assertTrue(new Interval(10, 0).isEmpty());
	}
}
