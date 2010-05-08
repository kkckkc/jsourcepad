package kkckkc.syntaxpane.parse.grammar.builder;

import kkckkc.syntaxpane.parse.grammar.CompoundContext;
import kkckkc.syntaxpane.parse.grammar.Context;


public abstract class CompoundContextBuilder<T extends CompoundContext, U extends CompoundContextBuilder<T, U>> 
	extends ContextBuilder<T, U> {
	
	public CompoundContextBuilder(T t) {
		super(t);
	}

	@SuppressWarnings("unchecked")
	public U childRefs(Context... references) {
		t.setChildReferences(references);
		return (U) this;
	}
}