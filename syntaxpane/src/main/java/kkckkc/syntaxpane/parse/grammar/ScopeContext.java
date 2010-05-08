package kkckkc.syntaxpane.parse.grammar;


import kkckkc.syntaxpane.model.Scope;
import kkckkc.syntaxpane.regex.Matcher;

public abstract class ScopeContext extends Context {
	public abstract Scope createScope(Scope parent, Matcher matcher);
}
