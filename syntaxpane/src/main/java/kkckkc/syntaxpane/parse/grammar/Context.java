package kkckkc.syntaxpane.parse.grammar;


public abstract class Context {
	protected String id;
	protected String name;
	protected Language language;
	protected boolean compiled = false;
	
	public Context() {
	}

	public Context(String id) {
		this.id = id;
	}

	public boolean isCompiled() {
		return compiled;
	}

	public void setCompiled(boolean compiled) {
		this.compiled = compiled;
	}

	public final String getName() {
		return name == null ? id : name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}
	
	public void compile() {
	}
}
