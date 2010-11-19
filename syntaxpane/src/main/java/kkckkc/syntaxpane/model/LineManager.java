package kkckkc.syntaxpane.model;

import kkckkc.syntaxpane.parse.CharProvider;
import kkckkc.syntaxpane.parse.grammar.RootContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.TreeSet;

public abstract class LineManager {
	protected CharProvider charProvider;
	protected TreeSet<Line> lines = new TreeSet<Line>();
	
	public LineManager(CharProvider charProvider) {
		this.charProvider = charProvider;
	}

    @NotNull
	public Line getLineByPosition(int position) {
		Line line = lines.floor(new Line(0, position, position));
        if (line != null) return line;
        return new Line(0, 0, 0);
	}

    @Nullable
	public Line getPrevious(@NotNull Line line) {
		return lines.lower(line);
	}

    @Nullable
	public Line getNext(@NotNull Line line) {
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
            if (scope == null) return new Scope(0, end - start, new RootContext(), null);
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

		public CharSequence getCharSequence(boolean includeLineEnd) {
            if (includeLineEnd) {
                return charProvider.getSubSequence(this.start, Math.min(charProvider.getLength(), this.end + 1));
            } else {
			    return charProvider.getSubSequence(this.start, this.end);
            }
		}
	}
}
