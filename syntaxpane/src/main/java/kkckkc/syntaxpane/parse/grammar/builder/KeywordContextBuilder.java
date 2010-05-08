package kkckkc.syntaxpane.parse.grammar.builder;

import kkckkc.syntaxpane.parse.grammar.KeywordContext;

public class KeywordContextBuilder extends MatchableContextBuilder<KeywordContext, KeywordContextBuilder> {
	public KeywordContextBuilder(KeywordContext t) {
		super(t);
	} 
	
	public KeywordContextBuilder keywords(String[] keywords) {
		t.setKeywords(keywords);
		return this;
	}
	
	public KeywordContextBuilder prefix(String prefix) {
		t.setPrefix(prefix);
		return this;
	}
	
	public KeywordContextBuilder suffix(String suffix) {
		t.setSuffix(suffix);
		return this;
	}
}
