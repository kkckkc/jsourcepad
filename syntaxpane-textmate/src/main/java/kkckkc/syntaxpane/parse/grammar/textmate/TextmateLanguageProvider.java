package kkckkc.syntaxpane.parse.grammar.textmate;

import kkckkc.syntaxpane.parse.grammar.Language;
import kkckkc.syntaxpane.parse.grammar.LanguageManager;
import kkckkc.syntaxpane.parse.grammar.RootContext;
import kkckkc.utils.io.FileUtils;
import kkckkc.utils.plist.PListReaderFacade;

import java.io.File;
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
		this.root = new File(FileUtils.expandTildeNotation(root));
	}
	
	public Map<String, Language> getLanguages(LanguageManager languageManager) {
		if (languages == null) reload(languageManager);
		return languages;
	}

	public void reload(LanguageManager languageManager) {
		languages = new HashMap<String, Language>();
		
		PListReaderFacade r = new PListReaderFacade();
		try {
	        recurse(languageManager, root, r);
        } catch (Exception e) {
	        throw new RuntimeException(e);
        }
	}
	
	private void recurse(LanguageManager languageManager, File file, PListReaderFacade r) throws IOException {
		for (File childFile : file.listFiles()) {
			if (childFile.getName().endsWith(".plist") || childFile.getName().endsWith(".tmLanguage")) {
				if (file.getName().equals("Syntaxes")) {
					TextmateLanguageParser lp = new TextmateLanguageParser(childFile);
					Language language = lp.parse();
					language.setLanguageManager(languageManager);
					languages.put(language.getLanguageId(), language);
				}
			} else if (childFile.isDirectory()) {
				recurse(languageManager, childFile, r);
			}
		}
	}
	
	@Override
	public Language getDefaultLanguage() {
		return DEFAULT_LANGUAGE;
	}

}
