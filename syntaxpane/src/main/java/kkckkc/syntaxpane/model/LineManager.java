package kkckkc.syntaxpane.model;

import kkckkc.syntaxpane.parse.CharProvider;
import kkckkc.syntaxpane.parse.grammar.RootContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;

public abstract class LineManager implements Iterable<LineManager.Line> {
	protected CharProvider charProvider;
	protected NavigableSet<Line> lines = new ConcurrentSkipListSet<Line>();
	protected int size;

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
		return size;
	}

	public Iterator<Line> iterator() {
		return lines.iterator();
	}	

    public Iterator<Line> iterator(int start, int end) {
        return lines.subSet(getLineByIdx(start), true, getLineByIdx(end), true).iterator();
    }

	public void dumpXml(StringBuffer b) {
		for (Line l : lines) {
			b.append(l.toXml()).append("\n");
		}
	}

	public String dumpXml() {
		StringBuffer buf = new StringBuffer();
		dumpXml(buf);
		return buf.toString();
	}

    public Line getLineByIdx(int lineIdx) {
        if (lineIdx == 0) {
            return lines.isEmpty() ? null : lines.first();
        }

        Line l = new Line(-lineIdx, 0, 0);
        return lines.lower(l);
    }

    public class Line extends Interval {
		protected int idx;
		protected Scope scope;
		protected long flags;

		public Line(int idx, int start, int end) {
			super(start, end);
			this.idx = idx;
            this.flags = 0;
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

        public long getFlags() {
            return flags;
        }

        public void setFlags(long flags) {
            this.flags = flags;
        }

        public String toString() {
			return String.format("%4d [%4d - %4d] %4d |%-100.100s|",
					idx,
					start,
					end,
                    flags,
					charProvider.getSubSequence(start, end).toString().replace("\t", "--->"));
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

        public void addFlag(int flag) {
            this.flags |= flag;
        }

        public void clearFlag(int flag) {
            this.flags &= ~flag;
        }

        public final int hashCode() {
    		return this.idx;
    	}

    	public final boolean equals(Object other) {
            return other instanceof Line && ((Line) other).idx == idx;
        }

        public int compareTo(Interval i) {
            Line o = (Line) i;
            if (idx < 0) {
                if (-idx < o.idx) return -1;
                else return 1;
            } else {
                return super.compareTo(i);
            }
        }
    }
}
