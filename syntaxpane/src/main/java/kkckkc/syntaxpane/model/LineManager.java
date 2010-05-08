package kkckkc.syntaxpane.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import kkckkc.syntaxpane.parse.CharProvider;
import kkckkc.syntaxpane.util.Pair;

public class LineManager {
	private CharProvider charProvider;
	private TreeSet<Line> lines = new TreeSet<Line>();
	
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
	
	
	public List<Line> addInterval(Interval interval) {
		Line startLine = getLineByPosition(interval.start);
		if (startLine == null) {
			startLine = new Line(0, 0, 0);
		}
		
		int idx = startLine.idx;
		List<Line> newLines = new ArrayList<Line>();
		Line l = nextLine(idx, startLine.start);
		newLines.add(l);
		while (l.end < (startLine.end + interval.getLength())) {
			l = nextLine(++idx, l.end + 1);
			newLines.add(l);
		}
		
		Line renumberFrom = lines.higher(startLine);
		if (renumberFrom != null) {
			renumber(renumberFrom, renumberFrom.idx + newLines.size() - 1, 
					renumberFrom.start, interval.getLength());
		}

		lines.remove(startLine);
		lines.addAll(newLines);
		
		return newLines;
	}

	public Pair<Line, Line> removeInterval(Interval interval) {
		Line startLine = getLineByPosition(interval.start);
		Line endLine = getLineByPosition(interval.end);

		List<Line> linesToRemove = new ArrayList<Line>();
		
		if (startLine != endLine) {
			Iterator<Line> it = lines.tailSet(startLine, false).iterator();
			while (it.hasNext()) {
				Line l = it.next();
				if (l == endLine) break;
				it.remove();
			}
			
			// Merge start and end
			startLine.end = endLine.end;
			linesToRemove.add(endLine);
		}

		lines.removeAll(linesToRemove);
		renumber(startLine, startLine.idx, interval.start, - interval.getLength());
		
		return new Pair<Line, Line>(startLine, endLine);
	}

	private Line nextLine(int idx, int start) {
		int c = charProvider.find(start, '\n');
		if (c == -1) {
			return new Line(idx, start, charProvider.getLength());
		} else {
			return new Line(idx, start, c);
		}
	}
	
	private void renumber(Line startLine, int idx, int position, int length) {
		Iterator<Line> it = lines.tailSet(startLine, true).iterator();
		while (it.hasNext()) {
			Line l = it.next();
			if (length >= 0) {
				if (l.start >= position) {
					l.start += length;
				}
			} else {
				if (l.start > position) {
					l.start += length;
				}
			}
			l.end += length;
			
			l.start = Math.max(l.start, 0);
			l.end = Math.max(l.end, 0);
			
			l.idx = idx;
			idx++;
		}
	}

	
	public void dump() {
		for (Line l : lines) {
			l.getScope().getRoot().dump();
		}
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
		private int idx;
		private Scope scope;
		
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
