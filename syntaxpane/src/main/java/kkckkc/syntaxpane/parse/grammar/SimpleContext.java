package kkckkc.syntaxpane.parse.grammar;

import kkckkc.syntaxpane.regex.Matcher;
import kkckkc.syntaxpane.regex.Pattern;

public class SimpleContext extends MatchableContext {
	protected Pattern pattern;
	
	public SimpleContext() {
	}
	
	public SimpleContext(String id) {
		this.id = id;
	}

	@Override
	public Matcher getMatcher(CharSequence seq) {
		return pattern.matcher(seq);
	}
	
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
}
