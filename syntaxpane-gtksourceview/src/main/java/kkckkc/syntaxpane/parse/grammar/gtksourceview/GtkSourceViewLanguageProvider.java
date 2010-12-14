package kkckkc.syntaxpane.parse.grammar.gtksourceview;

import kkckkc.syntaxpane.parse.grammar.Language;
import kkckkc.syntaxpane.parse.grammar.LanguageManager;
import kkckkc.syntaxpane.parse.grammar.RootContext;

import java.io.File;
import java.util.HashMap;
import java.util.Map;



public class GtkSourceViewLanguageProvider implements LanguageManager.Provider {

	private static Language DEFAULT_LANGUAGE;
	static {
		DEFAULT_LANGUAGE = new Language("default");
		DEFAULT_LANGUAGE.setRootContext(new RootContext("default"));
	}

	private HashMap<String, Language> languages;
	private File root;
	
	public GtkSourceViewLanguageProvider(File root) {
		this.root = root;
	}
	
	public Map<String, Language> getLanguages(LanguageManager languageManager) {
		if (languages == null) reload(languageManager);
		return languages;
	}

	public void reload(LanguageManager languageManager) {
		File[] languageFiles = root.listFiles();

		languages = new HashMap<String, Language>();
		
		for (final File languageFile : languageFiles) {
			if (! languageFile.getName().endsWith(".lang")) continue;
		
			GtkSourceViewLanguageParser languageParser = new GtkSourceViewLanguageParser(languageFile);
			try {
				Language language = languageParser.parse();
				language.setLanguageManager(languageManager);
				languages.put(language.getLanguageId(), language);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public Language getDefaultLanguage() {
		return DEFAULT_LANGUAGE;
	}

}
