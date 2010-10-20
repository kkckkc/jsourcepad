package kkckkc.syntaxpane.parse.grammar.textmate;

import kkckkc.syntaxpane.parse.grammar.Language;
import kkckkc.syntaxpane.parse.grammar.LanguageManager;
import kkckkc.syntaxpane.parse.grammar.RootContext;
import kkckkc.utils.io.FileUtils;
import kkckkc.utils.plist.GeneralPListReader;
import kkckkc.utils.plist.PListReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TextmateLanguageProvider implements LanguageManager.Provider {

	private static Language DEFAULT_LANGUAGE;
	static {
		DEFAULT_LANGUAGE = new Language("default");
		DEFAULT_LANGUAGE.setRootContext(new RootContext("default"));
	}

	private HashMap<String, Language> languages;
	private File root;
	
	public TextmateLanguageProvider(String root) {
		this.root = new File(FileUtils.expandAbbreviations(root));
	}
	
	public Map<String, Language> getLanguages(LanguageManager languageManager) {
		if (languages == null) reload(languageManager);
		return languages;
	}

	public void reload(LanguageManager languageManager) {
		languages = new HashMap<String, Language>();
		
		GeneralPListReader r = new GeneralPListReader();
		try {
	        recurse(languageManager, root, r);
        } catch (Exception e) {
	        throw new RuntimeException(e);
        }
	}
	
	private void recurse(LanguageManager languageManager, File file, PListReader r) throws FileNotFoundException, IOException {
		for (File f : file.listFiles()) {
			if (f.getName().endsWith(".plist") || f.getName().endsWith(".tmLanguage")) {
				if (file.getName().equals("Syntaxes")) {
					TextmateLanguageParser lp = new TextmateLanguageParser(f);
					Language language = lp.parse();
					language.setLanguageManager(languageManager);
					languages.put(language.getLanguageId(), language);
				}
			} else if (f.isDirectory()) {
				recurse(languageManager, f, r);
			}
		}
	}
	
	@Override
	public Language getDefaultLanguage() {
		return DEFAULT_LANGUAGE;
	}

}
