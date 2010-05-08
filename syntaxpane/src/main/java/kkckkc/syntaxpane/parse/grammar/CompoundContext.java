package kkckkc.syntaxpane.parse.grammar;

public abstract class CompoundContext extends ScopeContext {
	public static final Context[] EMPTY_CHILD_REFERENCES = new Context[] {};

	protected Context[] childReferences = EMPTY_CHILD_REFERENCES;

	public void setChildReferences(Context[] references) {
		this.childReferences = references;
	}

	public abstract Context[] getChildren();
}
