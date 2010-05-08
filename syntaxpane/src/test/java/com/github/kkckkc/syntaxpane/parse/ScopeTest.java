package com.github.kkckkc.syntaxpane.parse;


import junit.framework.TestCase;
import kkckkc.syntaxpane.model.Scope;
import kkckkc.syntaxpane.parse.grammar.builder.ContextBuilder;

public class ScopeTest extends TestCase {
	public void testPath() {
		Scope scope = new Scope(0, 100, ContextBuilder.simpleContext("root").build(), null);
		Scope inner = new Scope(10, 20, ContextBuilder.simpleContext("inner").build(), scope);
		
		assertEquals("root", scope.getPath());
		assertEquals("root inner", inner.getPath());
	}
	
}
