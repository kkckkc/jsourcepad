package kkckkc.syntaxpane.parse.grammar;


public class ReferenceContext extends Context {
	private String ref;

	public ReferenceContext() {
	}

	public ReferenceContext(String ref) {
		this.ref = ref;
	}

	public String getRef() {
		return ref;
	}
	
	public void setRef(String ref) {
		this.ref = ref;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
