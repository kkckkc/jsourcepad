package kkckkc.syntaxpane.parse.grammar;


import kkckkc.syntaxpane.model.Scope;
import kkckkc.syntaxpane.regex.Matcher;

public class SubPatternContext extends ScopeContext {
	public static enum Where { START, END }
	
	private static final int USE_PATTERN_NAME = -1;
	
	private String subPattern;
	private int subPatternIdx = USE_PATTERN_NAME;
	private Where where;
	
	public SubPatternContext() {
	}

	public SubPatternContext(String id) {
		this.id = id;
	}
	
	public int getSubPatternIdx() {
	    return subPatternIdx;
    }
	
	@Override 
	public Scope createScope(Scope parent, Matcher matcher) {
		int[] bounds = getBounds(matcher);
		if (bounds[0] == -1) return null;
		return new Scope(bounds[0], bounds[1], this, parent);
	}
	
	public int[] getBounds(Matcher matcher) {
		if (subPatternIdx != USE_PATTERN_NAME) {
			return new int[] { matcher.start(subPatternIdx), matcher.end(subPatternIdx) };
		} else {
			return new int[] { matcher.start(subPattern), matcher.end(subPattern) };
		}
	}

	public Where getWhere() {
		return where;
	}

	public void setWhere(Where where) {
		this.where = where;
	}

	public void setSubPattern(String subPattern) {
		try {
			this.subPatternIdx  = Integer.parseInt(subPattern);
		} catch (NumberFormatException nfe) {
			this.subPattern = subPattern;
		}
	}

}
