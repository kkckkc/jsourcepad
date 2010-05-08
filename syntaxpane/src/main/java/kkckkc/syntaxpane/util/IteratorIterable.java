package kkckkc.syntaxpane.util;

import java.util.Iterator;

public class IteratorIterable<T> implements Iterable<T> {
	private Iterator<T> iterator;
	
	public IteratorIterable(Iterator<T> iterator) {
		this.iterator = iterator;
	}

	public Iterator<T> iterator() {
		return this.iterator;
	}
}