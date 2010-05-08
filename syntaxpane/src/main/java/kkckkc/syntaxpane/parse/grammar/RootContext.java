package kkckkc.syntaxpane.parse.grammar;

import kkckkc.syntaxpane.regex.Matcher;

public class RootContext extends MatchableContext {

	public RootContext() {
	}

	public RootContext(String id) {
		this.id = id;
	}

	@Override
	public Matcher getMatcher(CharSequence s) {
		return null;
	}

}
