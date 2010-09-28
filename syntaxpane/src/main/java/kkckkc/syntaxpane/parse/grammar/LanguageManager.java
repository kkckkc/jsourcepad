package kkckkc.syntaxpane.parse.grammar;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LanguageManager {
	private Provider provider;

	public void setProvider(Provider provider) {
		this.provider = provider;
	}
	
	public Context resolveContext(Context ctx) {
		if (! (ctx instanceof ReferenceContext)) return ctx;
		
		String ref = ((ReferenceContext) ctx).getRef();
		
		if (ref.endsWith(":*")) {
			Language l;
			if (ref.indexOf(':') == ref.lastIndexOf(':')) {
				ref = ref.substring(0, ref.lastIndexOf(':'));
				l = ctx.getLanguage();
			} else {
				String langId = ref.substring(0, ref.indexOf(':'));
				ref = ref.substring(ref.indexOf(':') + 1, ref.lastIndexOf(':'));
				
				l = getLanguage(langId);
				if (l == null) {
					System.out.println("Cannot find language with id " + langId);
					return null;
				}
			}
			
			Context c = l.find(ref);
			if (c == null) {
				System.out.println("Couldn't find " + ref + " in language " + l);
				return null;
			}

			return c;
		} else if (ref.indexOf(':') >= 0) {
			String langId = ref.substring(0, ref.indexOf(':'));
			ref = ref.substring(ref.indexOf(':') + 1);

			Language l = getLanguage(langId);
			if (l == null) {
				System.out.println("Cannot find language with id " + langId);
				return null;
			}
			
			Context c = l.find(ref);

			if (c == null) {
				System.out.println("Couldn't find " + ref + " in language " + l);
				return null;
			}
			
			if (c.getLanguage() == null) {
				c.setLanguage(l);
			}
			
			return c;
		} else {
			Context c = ctx.getLanguage().find(ref);
			if (c == null) {
				RootContext rc = (RootContext) ctx.getLanguage().getRootContext();
				for (Context ch : rc.getChildren()) {
					System.out.println(ch);
				}
				System.out.println("Cannot find " + ref + ", in " + ctx.getLanguage());
				return null;
			}
			return c;
		}
	}

	public Language getLanguage(String langId) {
		Language l = provider.getLanguages(this).get(langId);
		if (l == null) return provider.getDefaultLanguage();
		if (! l.isCompiled()) {
			l.compile();
		}
		return l;
	}
	
	
	public interface Provider {
		public Map<String, Language> getLanguages(LanguageManager languageManager);
		public void reload(LanguageManager languageManager);
		public Language getDefaultLanguage();
	}


	public Iterable<Language> getLanguages() {
		List<Language> dest = new ArrayList<Language>();
		dest.addAll(provider.getLanguages(this).values());
		Collections.sort(dest);
		return dest;
	}

	public Language getLanguage(String firstLine, File file) {
		for (Language l : provider.getLanguages(this).values()) {
			if (l.matches(firstLine, file)) {
				l.compile();
				return l;
			}
		}
		return provider.getDefaultLanguage();
	}
}
