package kkckkc.syntaxpane.model;

import kkckkc.syntaxpane.parse.CharProvider;
import kkckkc.utils.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MutableLineManager extends LineManager {

	public MutableLineManager(CharProvider charProvider) {
	    super(charProvider);
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


	private void renumber(Line startLine, int idx, int position, int length) {
        for (Line line : lines.tailSet(startLine, true)) {
            Line l = line;
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
	
	
	private Line nextLine(int idx, int start) {
		int c = charProvider.find(start, '\n');
		if (c == -1) {
			return new Line(idx, start, charProvider.getLength());
		} else {
			return new Line(idx, start, c);
		}
	}

}
