package kkckkc.syntaxpane.parse.grammar.builder;

import kkckkc.syntaxpane.parse.grammar.SubPatternContext;
import kkckkc.syntaxpane.parse.grammar.SubPatternContext.Where;

public class SubPatternContextBuilder extends ContextBuilder<SubPatternContext, SubPatternContextBuilder> {
	public SubPatternContextBuilder(SubPatternContext t) {
		super(t);
	}
	
	public SubPatternContextBuilder where(Where where) {
		t.setWhere(where);
		return this;
	}
	
	public SubPatternContextBuilder subPattern(String subPattern) {
		t.setSubPattern(subPattern);
		return this;
	}
}