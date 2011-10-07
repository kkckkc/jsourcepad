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

	public Pair<Line, Line> intervalAdded(Interval interval) {
		Line startLine = getLineByPosition(interval.start);

		int idx = startLine.idx;
		List<Line> newLines = new ArrayList<Line>(Math.max(20, interval.getLength() / 10));
		Line l = nextLine(idx, startLine.start);
		newLines.add(l);
        int end = startLine.end + interval.getLength();
        while (l.end < end) {
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
		
		return new Pair<Line, Line>(newLines.get(0), newLines.get(newLines.size() - 1));
	}

	public Pair<Line, Line> intervalRemoved(Interval interval) {
		Line startLine = getLineByPosition(interval.start);
		Line endLine = getLineByPosition(interval.end);

		List<Line> linesToRemove = new ArrayList<Line>(endLine.getIdx() - startLine.getIdx() + 5);
		
		if (startLine != endLine) {
			Iterator<Line> it = lines.tailSet(startLine, false).iterator();
			while (it.hasNext()) {
				Line line = it.next();
				if (line == endLine) break;
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
            if (length >= 0) {
                if (line.start >= position) {
                    line.start += length;
                }
            } else {
                if (line.start > position) {
                    line.start += length;
                }
            }
            line.end += length;

            line.start = Math.max(line.start, 0);
            line.end = Math.max(line.end, 0);

            line.idx = idx;
            idx++;
        }
	}
	
	
	private Line nextLine(int idx, int start) {
		int newlineIdx = charProvider.find(start, '\n');
		if (newlineIdx == -1) {
			return new Line(idx, start, charProvider.getLength());
		} else {
			return new Line(idx, start, newlineIdx);
		}
	}

}
