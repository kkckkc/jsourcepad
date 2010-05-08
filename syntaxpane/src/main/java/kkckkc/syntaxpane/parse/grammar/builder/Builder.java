package kkckkc.syntaxpane.parse.grammar.builder;

import kkckkc.syntaxpane.parse.grammar.Context;

public interface Builder<T extends Context> { 
	public T build();
}