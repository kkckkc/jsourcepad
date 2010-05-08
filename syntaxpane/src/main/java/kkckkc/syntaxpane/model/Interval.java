package kkckkc.syntaxpane.model;

public class Interval implements Comparable<Interval> {
	protected int start;
	protected int end;
	
	public Interval(int start, int end) {
		if (end < start) {
			this.start = end;
			this.end = start;
		} else {
			this.start = start;
			this.end = end;
		}
	}

	public final int getStart() {
		return start;
	}
	
	public final int getEnd() {
		return end;
	}
	
	public final boolean contains(int i) {
		return i >= start && i <= end;
	}
	
	public final boolean contains(Interval oi) {
		return oi.start >= this.start && oi.end <= this.end;
	}
	
	public final boolean overlaps(Interval oi) {
		return this.start <= oi.end && oi.start <= this.end;
	}
	
	public final Interval overlap(Interval oi) {
		return new Interval(
					Math.max(this.start, oi.getStart()),
					Math.min(this.end, oi.getEnd()));
	}
	
	public final int hashCode() {
		return this.start << 8 | this.end;
	}
	
	public final boolean isEmpty() {
		return end <= start;
	}
	
	public final boolean equals(Object other) {
		if (other instanceof Interval) {
			return 
				((Interval) other).start == start &&
				((Interval) other).end == end;
		}
		return false;
	}
	
	public String toString() {
		return "[" + start + " - " + end + "]";
	}

	public final int compareTo(Interval o) {
		return start - o.start;
	}

	public final int getLength() {
		return end - start;
	}

	public static Interval createWithLength(int start, int len) {
	    return new Interval(start, start + len);
    }

	public static Interval createEmpty(int start) {
	    return createWithLength(start, 0);
    }
}
