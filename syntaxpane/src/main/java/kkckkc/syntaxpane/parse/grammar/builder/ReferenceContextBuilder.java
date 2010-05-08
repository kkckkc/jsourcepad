package kkckkc.syntaxpane.parse.grammar.builder;

import kkckkc.syntaxpane.parse.grammar.ReferenceContext;

public class ReferenceContextBuilder extends ContextBuilder<ReferenceContext, ReferenceContextBuilder> {
	
	public ReferenceContextBuilder(ReferenceContext t) {
		super(t);
	}
	
	public ReferenceContextBuilder id(String id) {
		t.setId(id);
		return this;
	}
	
	public ReferenceContextBuilder ref(String ref) {
		t.setRef(ref);
		return this;
	}
}