package kkckkc.syntaxpane.parse.grammar.builder;


import kkckkc.syntaxpane.parse.grammar.ContainerContext;
import kkckkc.syntaxpane.regex.Pattern;

public class ContainerContextBuilder extends MatchableContextBuilder<ContainerContext, ContainerContextBuilder> {
	public ContainerContextBuilder(ContainerContext t) {
		super(t);
	}

	public ContainerContextBuilder begin(Pattern pattern) {
		t.setBegin(pattern);
		return this;
	}

	public ContainerContextBuilder end(Pattern pattern) {
		t.setEnd(pattern);
		return this;
	}
	
	public ContainerContextBuilder styleInside(boolean b) {
		t.setStyleInside(b);
		return this;
	}
	
	public ContainerContextBuilder endAtLineEnd(boolean b) {
		t.setEndAtLineEnd(b);
		return this;
	}

	public ContainerContextBuilder styleInside() {
		t.setStyleInside(true);
		return this;
	}

	public ContainerContextBuilder endAtLineEnd() {
		t.setEndAtLineEnd(true);
		return this;
	}
}