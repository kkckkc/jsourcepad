package kkckkc.syntaxpane.parse.grammar;

import kkckkc.syntaxpane.regex.Matcher;
import kkckkc.syntaxpane.regex.NamedPatternFactory;
import kkckkc.syntaxpane.regex.Pattern;
import kkckkc.syntaxpane.regex.PatternFactory;


public class KeywordContext extends MatchableContext {

	private Pattern pattern;
	protected String[] keywords;
	protected String prefix;
	protected String suffix;
	private PatternFactory factory;
	

	public KeywordContext(PatternFactory factory) {
		this.factory = factory;
	}
	
	public KeywordContext(PatternFactory factory, String id) {
		this.id = id;
		this.factory = factory;
	}

	@Override
	public Matcher getMatcher(CharSequence s) {
		if (pattern == null) {
			buildPattern();
		}
		
		return pattern.matcher(s);
	}


	public String[] getKeywords() {
		return keywords;
	}

	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	private void buildPattern() {
		StringBuilder builder = new StringBuilder();

		builder.append("\\b(");
		
		for (String s : keywords) {
			builder.
				append(prefix == null ? "" : prefix).append(s).append(suffix == null ? "" : suffix).
				append("|");
		}
		
		builder.setLength(builder.length() - 1);
		
		builder.append(")\\b");
		
		this.pattern = factory.create(builder.toString());
	}

	@Override
	public void compile() {
		super.compile();
	}
	
	
}
