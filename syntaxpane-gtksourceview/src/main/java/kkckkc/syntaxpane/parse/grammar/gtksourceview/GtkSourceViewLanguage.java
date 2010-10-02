package kkckkc.syntaxpane.parse.grammar.gtksourceview;

import kkckkc.syntaxpane.parse.grammar.Language;

import java.util.Map;



public class GtkSourceViewLanguage extends Language {

	private Map<String, String> definedRegexps;
	private Map<String, String> styles;

	public GtkSourceViewLanguage(String languageId) {
		super(languageId);
	}

	public void setDefinedRegexps(Map<String, String> regexps) {
		this.definedRegexps = regexps;
	}

	public Map<String, String> getDefinedRegexps() {
		return definedRegexps;
	}

	public void setStyles(Map<String, String> styles) {
		this.styles = styles;
	}
	
	public Map<String, String> getStyles() {
		return styles;
	}

	public String resolveStyle(String name) {
		String mapTo = getStyles().get(name);
		
		if (mapTo == null || "".equals(mapTo)) {
			return languageId + ":" + name;
		}
		
		String languagePart = languageId;
		if (mapTo.indexOf(':') >= 0) {
			languagePart = mapTo.substring(0, mapTo.indexOf(':'));
			mapTo = mapTo.substring(mapTo.indexOf(':') + 1);
		}
		
		GtkSourceViewLanguage l = (GtkSourceViewLanguage) getLanguageManager().getLanguage(languagePart);
		
		if (l == null) {
			return mapTo + "." + languageId + ":" + name;
		} else {
			return l.resolveStyle(mapTo) + "." + languageId + ":" + name;
		}
	}
}
