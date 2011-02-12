package kkckkc.syntaxpane.model;

import kkckkc.syntaxpane.parse.grammar.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class Scope {
    private int start;
    private int end;
	private List<Scope> children;
	private Context context;
	private Scope parent;

	public Scope(int start, int end, Context context, Scope parent) {
		this.start = Math.min(start, end);
		this.end = Math.max(start, end);

		this.children = null; 
		this.context = context;
		this.parent = parent;
		if (parent != null) {
			parent.addChild(this);
		}
	}

	private void addChild(Scope scope) {
		if (children == null) {
			children = new ArrayList<Scope>();
		}
		children.add(scope);
		scope.parent = this;
	}

	public final Scope getParent() {
		return parent;
	}

	public final Context getContext() {
		return context;
	}

	public final List<Scope> getChildren() {
		return children;
	}

	public final void close(int offset) {
		this.end = offset;
	}

	public final Scope getForPosition(int o) {
		if (children == null) return this;
		for (Scope child : children) {
			if (child.contains(o)) {
				return child.getForPosition(o);
			}
		}
		return this;
	}

    public final boolean contains(int i) {
        return i >= start && i <= end;
    }

    public final int getStart() {
		return start;
	}

	public final int getEnd() {
		return end;
	}

	public final String getPath() {
		StringBuilder b = new StringBuilder(60);
		buildPath(b);
		return b.toString();
	}

	private void buildPath(StringBuilder b) {
		String n = context.getName();
		if (parent != null) {
			parent.buildPath(b);
			if (n != null) b.append(" ");
		}
		if (n != null) b.append(n);
	}

	public final StringBuilder toXml(CharSequence s) {
		StringBuilder b = new StringBuilder();
		b.append("<").append(context.getId()).append(">");
		if (children == null || children.isEmpty()) {
			int end = this.end;
			if (end == -1) {
				b.append(safesubstring(s, start, s.length()));
			} else {
				b.append(safesubstring(s, start, end));
			}
		} else {
			int o = start;
			for (Scope c : children) {
				if (c.start > 0)
					b.append(safesubstring(s, o, c.start));
				b.append(c.toXml(s));
				o = c.end;
			}

			int end = this.end;
			int start = children.get(children.size() - 1).end;
			if (end == -1) {
				b.append(safesubstring(s, start, s.length()));
			} else {
				b.append(safesubstring(s, start, s.length()));
			}
		}
		b.append("</").append(context.getId()).append(">");
		return b;
	}

	private CharSequence safesubstring(CharSequence s, int start, int end) {
		start = Math.min(s.length(), Math.max(start, 0));
		end = Math.min(s.length(), end);
		return s.subSequence(start, end);
	}

	public final String toString() {
		return super.toString() + " " + getPath() + " ("
				+ System.identityHashCode(this) + ")";
	}

	public Scope copy() {
		return new Scope(Integer.MAX_VALUE, Integer.MIN_VALUE, getContext(), parent == null ? null : parent.copy());
	}

	public final Iterable<Scope> getAncestors() {
		return new ScopeAncestorIterator();
	}

	public final Scope getRoot() {
		Scope scope = this;
		while (scope.parent != null) {
			scope = scope.parent;
		}
		return scope;
	}

	public final boolean hasChildren() {
		return children != null && ! children.isEmpty();
	}

	public final boolean hasSameSignature(Scope scope) {
		Scope current = this;
		Scope compareWith = scope;
		
		while (true) {
			String i1 = current.getContext().getId();
			if (i1 == null) i1 = "_";
			String i2 = compareWith.getContext().getId();
			if (i2 == null) i2 = "_";

			if (! i1.equals(i2))
				return false;
			
			if (current.getParent() != null && compareWith.getParent() != null) {
				current = current.getParent();
				compareWith = compareWith.getParent();
				continue;
			}

			return current.getParent() == compareWith.getParent();
		}
	}

	public final void makeOpenEnded() {
		this.end = Integer.MAX_VALUE;
	}

    public Scope getLastChild() {
        return children.get(children.size() - 1);
    }

    private class ScopeAncestorIterator implements Iterator<Scope>, Iterable<Scope> {
        private Scope current = Scope.this;

        public boolean hasNext() {
            return current != null;
        }

        public Scope next() {
            Scope scope = current;
            current = current.parent;
            return scope;
        }

        public void remove() {
        }

        @Override
        public Iterator<Scope> iterator() {
            return this;
        }
    }
}
