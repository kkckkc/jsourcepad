package kkckkc.syntaxpane.parse.grammar;

import com.google.common.base.Supplier;
import kkckkc.syntaxpane.regex.Pattern;

import java.io.File;
import java.util.HashMap;
import java.util.Map;




public class Language implements Comparable<Language> {
	private Map<String, Context> cache; 

	private Context rootContext;
	private Context[] supportingContexts = new Context[] {};
	protected String languageId;
	
	private LanguageManager languageManager;
	private boolean compiled;

	private Supplier<Pattern> firstLinePattern = null;
	private Supplier<Pattern> fileNamePattern = null;
	private Supplier<Pattern> foldStart;
	private Supplier<Pattern> foldEnd;

	private String name;

	
	public Language(String languageId) {
		this.languageId = languageId;
	}

	public boolean isStandalone() {
		return rootContext != null;
	}
	
	public boolean matches(String firstLine, File file) {
		if (firstLinePattern != null && firstLinePattern.get().matcher(firstLine).matches()) return true;
		if (fileNamePattern != null && fileNamePattern.get().matcher(file.getName()).matchesAll()) return true;
		
		return false;
	}
	
	public void setFileNamePattern(Supplier<Pattern> fileNamePattern) {
		this.fileNamePattern = fileNamePattern;
	}
	
	public void setFirstLinePattern(Supplier<Pattern> firstLinePattern) {
		this.firstLinePattern = firstLinePattern;
	}
	
	public void setFoldStart(Supplier<Pattern> foldStart) {
	    this.foldStart = foldStart;
    }
	
	public Pattern getFoldStart() {
		return foldStart == null ? null : foldStart.get();
	}
	
	public void setFoldEnd(Supplier<Pattern> foldEnd) {
	    this.foldEnd = foldEnd;
    }
	
	public Pattern getFoldEnd() {
		return foldEnd == null ? null : foldEnd.get();
	}
	
	
	public boolean isCompiled() {
		return compiled;
	}
	
	public String getLanguageId() {
		return languageId;
	}

	public void setSupportingContexts(Context[] supportingContexts) {
		this.supportingContexts = supportingContexts;
	}
	
	public Context[] getSupportingContexts() {
		return supportingContexts;
	}
	
	public void setRootContext(Context rootContext) {
		this.rootContext = rootContext;
	}
	
	public Context getRootContext() {
		return rootContext;
	}

	public void setLanguageManager(LanguageManager languageManager) {
		this.languageManager = languageManager;
	}
	
	public LanguageManager getLanguageManager() {
		return languageManager;
	}
	
	public void compile() {
		if (isCompiled()) return;
		
		if (languageManager == null) {
			throw new IllegalStateException("LanguageManager needs to be set to compile");
		}
		
		if (! isStandalone()) return;
		
		rootContext.setLanguage(this);
		rootContext.compile();
		
		for (Context c : supportingContexts) {
			c.setLanguage(this);
			c.compile();
		}
		
		this.compiled = true;
	}

	public Context find(String id) {
		if (cache == null) {
			buildCache();
		}
		return cache.get(id);
	}
	
	private synchronized void buildCache() {
	    cache = new HashMap<String, Context>(60);
	    
	    if (isStandalone()) {
			buildCache(getRootContext());
		}
		
		for (Context aux : supportingContexts) {
			buildCache(aux);			
		}
    }

	private void buildCache(Context c) {
		if (! (c instanceof CompoundContext)) return;
		if (cache.containsKey(c.getId())) return;
		
		cache.put(c.getId(), c);
		
		for (Context child : ((CompoundContext) c).getChildren()) {
			if (child == null) continue;
			
			buildCache(child);
		}
    }

	public String toString() {
		return languageId;
	}

	@Override
	public int compareTo(Language o) {
		return languageId.compareTo(o.languageId);
	}

	public void setName(String name) {
	    this.name = name;
    }
	
	public String getName() {
	    return name == null ? getLanguageId() : name;
    }
}
