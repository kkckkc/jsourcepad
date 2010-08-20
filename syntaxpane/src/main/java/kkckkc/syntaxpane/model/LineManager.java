package kkckkc.syntaxpane.model;

import java.util.Iterator;
import java.util.TreeSet;

import kkckkc.syntaxpane.parse.CharProvider;

public abstract class LineManager {
	protected CharProvider charProvider;
	protected TreeSet<Line> lines = new TreeSet<Line>();
	
	public LineManager(CharProvider charProvider) {
		this.charProvider = charProvider;
	}

	public Line getLineByPosition(int position) {
		return lines.floor(new Line(0, position, position));
	}

	public Line getPrevious(Line line) {
		return lines.lower(line);
	}

	public Line getNext(Line line) {
		return lines.higher(line);
	}

	public int size() {
		return lines.size();
	}

	public Iterator<Line> iterator() {
		return lines.iterator();
	}	
	
	public void dumpXml(StringBuffer b) {
		for (Line l : lines) {
			b.append(l.toXml()).append("\n");
		}
	}

	public String dumpXml() {
		StringBuffer b = new StringBuffer();
		dumpXml(b);
		return b.toString();
	}
	
	public class Line extends Interval {
		protected int idx;
		protected Scope scope;
		
		public Line(int idx, int start, int end) {
			super(start, end);
			this.idx = idx;
		}

		public Scope getScope() {
			return scope;
		}

		public void setScope(Scope scope) {
			this.scope = scope;
		}
		
		public int getIdx() {
			return idx;
		}
		
		public String toString() {
			return String.format("%2d [%4d - %4d]   %s", 
					idx,
					start,
					end,
					charProvider.getSubSequence(start, end));
		}
		 
		public String toXml() {
			return scope.getRoot().toXml(charProvider.getSubSequence(start, end)).toString();
		}

		public CharSequence getCharSequence() {
			return charProvider.getSubSequence(this.start, this.end);
		}
	}
}
