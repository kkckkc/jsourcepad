package kkckkc.syntaxpane.parse.grammar.builder;


import kkckkc.syntaxpane.parse.grammar.SimpleContext;
import kkckkc.syntaxpane.regex.Pattern;

public class SimpleContextBuilder extends MatchableContextBuilder<SimpleContext, SimpleContextBuilder> {
	public SimpleContextBuilder(SimpleContext t) {
		super(t);
	} 
	
	public SimpleContextBuilder match(Pattern pattern) {
		t.setPattern(pattern);
		return this;
	}
	
}
