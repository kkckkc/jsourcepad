package kkckkc.syntaxpane.parse.grammar;

import kkckkc.syntaxpane.model.Scope;
import kkckkc.syntaxpane.parse.grammar.SubPatternContext.Where;
import kkckkc.syntaxpane.regex.Matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
 
public abstract class MatchableContext extends CompoundContext {
	public static final Context[] EMPTY_CHILDREN = new Context[] {};
	
	protected boolean endParent;
	protected boolean firstLineOnly;
	protected boolean onceOnly;
	protected boolean extendParent;
	protected Context[] children = EMPTY_CHILDREN;
	protected MatchableContext[] unnestedChildren = null;
	
	public Context[] getChildren() {
		return children;
	}

	public MatchableContext[] getUnnestedMatchableChildren() {
		if (unnestedChildren == null) {
			List<MatchableContext> unnested = new ArrayList<MatchableContext>(30);
			unnest(unnested);
			this.unnestedChildren = unnested.toArray(new MatchableContext[unnested.size()]);
		}
		return unnestedChildren;
	}

	public boolean isEndParent() {
		return endParent;
	}

	public void setEndParent(boolean endsParent) {
		this.endParent = endsParent;
	}

	public boolean isFirstLineOnly() {
		return firstLineOnly;
	}

	public void setFirstLineOnly(boolean firstLineOnly) {
		this.firstLineOnly = firstLineOnly;
	}

	public boolean isOnceOnly() {
		return onceOnly;
	}

	public void setOnceOnly(boolean onceOnly) {
		this.onceOnly = onceOnly;
	}

	public boolean isExtendParent() {
		return extendParent;
	}

	public void setExtendParent(boolean extendParent) {
		this.extendParent = extendParent;
	}
	
	public Scope createScope(Scope parent, Matcher matcher) {
		Scope s = new Scope(matcher.start(), matcher.end(), this, parent);
		buildSubPatternScopes(s, matcher, null);
		return s;
	}

	public abstract Matcher getMatcher(CharSequence s);
	
	protected void buildSubPatternScopes(Scope parent, Matcher matcher, Where where) {
		if (children == null) return;

		Stack<Scope> scopeStack = null; 
		
		for (Context c : getChildren()) {
			if (c instanceof SubPatternContext) {
				if (scopeStack == null) {
					scopeStack = new Stack<Scope>();
					scopeStack.add(parent);					
				}
				
				SubPatternContext sbc = (SubPatternContext) c;
				if (sbc.getWhere() != where) continue;
				
				int[] bounds = sbc.getBounds(matcher);
				if (bounds[0] == -1) continue;
				
				Scope top = scopeStack.peek();
				if (! isSubScope(top, bounds)) {
					do {
						top = scopeStack.pop();
						
					} while (! isSubScope(top, bounds));
					scopeStack.push(top);
				}
				scopeStack.push(sbc.createScope(top, matcher));	
			}
		}
	}

	private boolean isSubScope(Scope scope, int[] bounds) {
	    return scope.contains(bounds[0]) && scope.contains(bounds[1]);
    }

	@Override
	public void compile() {
		if (isCompiled()) return;
		super.compile();
		compiled = true;
		
		this.children = new Context[childReferences.length];
        for (int i = 0; i < childReferences.length; i++) {
            childReferences[i].setLanguage(language);
            children[i] = language.getLanguageManager().resolveContext(childReferences[i]);

            if (children[i] == null) {
                // Ignore
            } else if (children[i].getLanguage() == null) {
                children[i].setLanguage(language);
            }
        }

        for (Context child : children) {
            if (child == null) continue;
            child.compile();
        }

		childReferences = null;	
	}

	void unnest(List<MatchableContext> dest) {
		for (Context context : children) {
			if (context == null) {
				continue;
			}

			if (context instanceof ContainerContext && ((ContainerContext) context).beginPattern == null) {
				((ContainerContext) context).unnest(dest);
            } else if (context instanceof RootContext) {
                ((RootContext) context).unnest(dest);
			} else if (context instanceof MatchableContext) {
				dest.add((MatchableContext) context);
			}
		}
	}
}
