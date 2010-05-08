package kkckkc.syntaxpane.parse.grammar.builder;

import kkckkc.syntaxpane.parse.grammar.MatchableContext;

public abstract class MatchableContextBuilder<T extends MatchableContext, U extends MatchableContextBuilder<T, U>> 
	extends CompoundContextBuilder<T, U> {
	
	public MatchableContextBuilder(T t) {
		super(t);
	}

	@SuppressWarnings("unchecked")
	public U endParent(boolean b) {
		t.setEndParent(b);
		return (U) this;
	}
	
	@SuppressWarnings("unchecked")
	public U firstLineOnly(boolean b) {
		t.setFirstLineOnly(b);
		return (U) this;
	}
	
	@SuppressWarnings("unchecked")
	public U onceOnly(boolean b) {
		t.setOnceOnly(b);
		return (U) this;
	}
	
	@SuppressWarnings("unchecked")
	public U extendParent(boolean b) {
		t.setExtendParent(b);
		return (U) this;
	}

	@SuppressWarnings("unchecked")
	public U endParent() {
		t.setEndParent(true);
		return (U) this;
	}
	
	@SuppressWarnings("unchecked")
	public U firstLineOnly() {
		t.setFirstLineOnly(true);
		return (U) this;
	}
	
	@SuppressWarnings("unchecked")
	public U onceOnly() {
		t.setOnceOnly(true);
		return (U) this;
	}
	
	@SuppressWarnings("unchecked")
	public U extendParent() {
		t.setExtendParent(true);
		return (U) this;
	}
}
