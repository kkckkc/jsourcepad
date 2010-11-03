package kkckkc.syntaxpane.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kkckkc.syntaxpane.parse.grammar.Context;



public class Scope extends Interval {
	private ArrayList<Scope> children;
	private Context context;
	private Scope parent;
	private HashMap<String, String> attributes;
	
	public Scope(int start, int end, Context context, Scope parent) {
		super(start, end);
		this.children = null; 
		this.context = context;
		this.parent = parent;
		if (parent != null) {
			parent.addChild(this);
		}
	}

	private void addChild(Scope scope) {
		if (children == null) {
			children = new ArrayList<Scope>(10);
		}
		children.add(scope);
		scope.parent = this;
	}

	public Scope getParent() {
		return parent;
	}

	public Context getContext() {
		return context;
	}

	public List<Scope> getChildren() {
		return children;
	}

	public void close(int offset) {
		this.end = offset;
	}

	public Scope getForPosition(int o) {
		if (children == null) return this;
		for (Scope child : children) {
			if (child.contains(o)) {
				return child.getForPosition(o);
			}
		}
		return this;
	}

	public String getPath() {
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

	public void dump() {
		dump(0);
	}

	private void dump(int lvl) {
		for (int i = 0; i < (lvl * 4); i++) {
			System.out.print(" ");
		}
		System.out.println(this);
		if (children == null) return;
		for (Scope c : children) {
			c.dump(lvl + 1);
		}
	}

	public StringBuilder toXml(CharSequence s) {
		StringBuilder b = new StringBuilder();
		b.append("<").append(context.getId()).append(">");
		if (children == null || children.isEmpty()) {
			int end = getEnd();
			if (end == -1) {
				b.append(safesubstring(s, start, s.length()));
			} else {
				b.append(safesubstring(s, start, end));
			}
		} else {
			int o = start;
			for (Scope c : children) {
				if (c.getStart() > 0)
					b.append(safesubstring(s, o, c.getStart()));
				b.append(c.toXml(s));
				o = c.getEnd();
			}

			int end = getEnd();
			int start = children.get(children.size() - 1).getEnd();
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

	public String toString() {
		return super.toString() + " " + getPath() + " ("
				+ System.identityHashCode(this) + ")";
	}

	public Scope copy(int start, int end) {
		Scope copy;
		if (parent == null) {
			copy = new Scope(start, end, getContext(), null);
		} else {
			copy = new Scope(start, end, getContext(), parent.copy(start, end));
		}
		if (this.attributes != null) {
			copy.attributes = new HashMap<String, String>(attributes);
		}
		return copy;
	}

	public Iterable<Scope> getAncestors() {
		return new Iterable<Scope>() {
			public Iterator<Scope> iterator() {
				return new Iterator<Scope>() {
					private Scope current = Scope.this;

					public boolean hasNext() {
						return current != null;
					}

					public Scope next() {
						Scope s = current;
						current = current.parent;
						return s;
					}

					public void remove() {
					}
				};
			}
		};
	}

	public Scope getRoot() {
		Scope s = this;
		while (s.parent != null) {
			s = s.parent;
		}
		return s;
	}

	public boolean hasChildren() {
		return children != null && ! children.isEmpty();
	}

	public boolean hasSameSignature(Scope scope) {
		Scope current = this;
		Scope compareWith = scope;
		
		while (true) {
			String i1 = current.getContext().getId();
			if (i1 == null) i1 = "none";
			String i2 = compareWith.getContext().getId();
			if (i2 == null) i2 = "none";
			
			if (! i1.equals(i2))
				return false;
			
			if (current.getParent() != null && compareWith.getParent() != null) {
				current = current.getParent();
				compareWith = compareWith.getParent();
				continue;
			}
			
			if (current.getParent() == null && compareWith.getParent() == null) {
				return true;
			}
			
			return false;
		}
	}

	public void makeOpenEnded() {
		this.end = Integer.MAX_VALUE;
	}

	public void addAttribute(String key, String value) {
		if (this.attributes == null) {
			this.attributes = new HashMap<String, String>();
		}
		this.attributes.put(key, value);
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}
}
