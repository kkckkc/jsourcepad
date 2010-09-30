package kkckkc.syntaxpane.model;

import kkckkc.syntaxpane.model.LineManager.Line;
import kkckkc.syntaxpane.regex.Pattern;
import kkckkc.syntaxpane.util.CharSequenceUtils;

import java.util.*;



public class MutableFoldManager implements FoldManager {
	private List<Fold> folds = new ArrayList<Fold>();
	
	private BitSet foldable = new BitSet();
	private List<FoldListener> foldListeners = new ArrayList<FoldListener>();
	private LineManager lineManager;
	
	private Pattern foldEndPattern;
	
	public MutableFoldManager(LineManager lineManager) {
		this.lineManager = lineManager;
	}
	
	public void setFoldEndPattern(Pattern foldEndPattern) {
		this.foldEndPattern = foldEndPattern;
	}

    @Override
    public Line getClosestFoldableLine(int position) {
        Line line = lineManager.getLineByPosition(position);
        while (line != null && ! foldable.get(line.getIdx())) {
            line = lineManager.getPrevious(line);
        }
        return line;
    }

	@Override
    public void toggle(Line line) {
		Interval fold = getFoldStartingWith(line.getIdx());
		
		if (fold != null) {
			unfold(line.getIdx());
		} else {
			fold(line);
		}
	}

	@Override
    public Fold getFoldStartingWith(int id) {
		Fold f = null;
		for (Fold i : folds) {
			if (i.getStart() == id) {
				f = i;
			}
		}
		
		return f;
	}
	
	// Optimization as folds are most often searched linearly idx by idx
	private Interval cachedFold = null;
	@Override
    public Interval getFoldOverlapping(int id) {
		if (cachedFold != null && cachedFold.contains(id)) return cachedFold;
		for (Interval f : folds) {
			if (f.getStart() > id) {
				break;
			} else if (f.contains(id)) { 
				cachedFold = f;
				return f;
			} 
		}
		return null;
	}
	
	@Override
    public State getFoldState(int idx) {
		Interval f = getFoldOverlapping(idx);
		if (f != null && f.getStart() == idx) {
			return State.FOLDED_FIRST_LINE;
		} else if (f != null) {
			return State.FOLDED_SECOND_LINE_AND_REST;
		} else if (foldable.get(idx)) {
			return State.FOLDABLE;
		} else {
			return State.DEFAULT;
		}
	}
	
	@Override
    public void fold(Line line) {
		if (! foldable.get(line.getIdx())) return;
		
		synchronized (folds) {
			List<Fold> newFolds = new ArrayList<Fold>(folds.size() + 1);
			
			Fold newFold = new Fold(line.getIdx(), getEndOfFold(line));
			
			List<Fold> children = new ArrayList<Fold>();

			boolean inserted = false;
			for (Fold f : folds) {
				if (newFold.contains(f)) {
					children.add(f);
				} else {
					if (! inserted && f.getStart() >= newFold.getStart()) {
						newFolds.add(newFold);
						inserted = true;
					}
					newFolds.add(f);
				}
			}

			if (! inserted) {
				newFolds.add(newFold);
			}
			
			newFold.setChildren(children);
			
			folds = newFolds;
		}
		fireFoldUpdated();
	}

	@Override
    public void unfold(int line) {
		Fold fold = getFoldStartingWith(line);
		if (fold == null) return;
		
		folds.remove(fold);
		
		folds.addAll(fold.getChildren());
		Collections.sort(folds);
		
		fireFoldUpdated();
	}
	

	private int getEndOfFold(Line line) {
		String s = line.getCharSequence().toString();
		int i = 0;
		while (Character.isWhitespace(s.charAt(i))) {
			i++;
		}
		String prefix = s.substring(0, i);
		
		int end = Integer.MAX_VALUE;
		Line l = lineManager.getNext(line);
		while (l != null) {
			end = l.getIdx();

			CharSequence lc = l.getCharSequence();
			if (CharSequenceUtils.startsWith(lc, prefix) && 
					foldEndPattern.matcher(lc).matches() &&
					! Character.isWhitespace(lc.charAt(prefix.length()))) {
				break;
			}
			l = lineManager.getNext(l);
		}
		return end;
	}

	public int toVisibleIndex(int line) {
		int offset = 0;
		for (Interval fold : folds) {
			if (fold.getEnd() < line) {
				offset += fold.getLength();
			}
		}
		return line - offset;
	}

	public int fromVisibleIndex(int line) {
		for (Interval fold : folds) {
			if (fold.getStart() < line) {
				line += fold.getLength();
			} else {
				break;
			}
		}
		return line;
	}

	public int getVisibleLineCount() {
		int lineCount = lineManager.size();
		for (Interval fold : folds) {
			lineCount -= fold.getLength();
		}
		return lineCount;
	}

	public int getLineCount() {
		return lineManager.size();
	}

	public boolean setFoldableFlag(int idx, boolean b) {
		if (b) {
			if (foldable.get(idx)) return false;
			foldable.set(idx, b);
			return true;
		} else {
			if (! foldable.get(idx)) return false;
			foldable.clear(idx);
			if (getFoldState(idx) == State.FOLDED_FIRST_LINE) {
				unfold(idx);
			}
			return true;
		}
	}
	
	public void fireFoldUpdated() {
		cachedFold = null;
		for (FoldListener l : foldListeners) {
			l.foldUpdated();
		}
	}
	
	public void addFoldListener(FoldListener foldListener) {
		this.foldListeners.add(foldListener);
	}
	
	public interface FoldListener extends EventListener {
		public void foldUpdated();
	}


	public boolean removeLines(Interval interval) {
		if (interval.isEmpty()) return false;
		if (folds.isEmpty()) return false;
		
		boolean ret = false;
		
		List<Fold> newFolds = new ArrayList<Fold>(Math.max(10, folds.size()));
			
		synchronized (folds) {
			for (Fold i : folds) {
				if (i.overlaps(interval)) {
					i.updateForDelete(interval, newFolds);
					
					ret = true;
					continue;
				} else if (i.getStart() > interval.getEnd()) {
					i.move(- interval.getLength());
					newFolds.add(i);
					ret = true;
				} else {
					newFolds.add(i);
				}
			}
			
			if (ret) {
				folds = newFolds;
			}
		}
		
		return ret;
	}

	public boolean addLines(Interval interval) {
		if (folds.isEmpty()) return false;

		boolean ret = false;
		
		List<Fold> newFolds = new ArrayList<Fold>(Math.max(10, folds.size() + interval.getLength()));

		synchronized (folds) {
			for (Fold i : folds) {
				if (i.getStart() > interval.getStart()) {
					i.move(interval.getLength());
					newFolds.add(i);
					ret = true;
				} else {
					newFolds.add(i);
				}
			}
			
			if (ret) {
				folds = newFolds;
			}
		}
		
		return ret;
	}
	
	
	static class Fold extends Interval {
		private List<Fold> children;
		
		public Fold(int start, int end) {
			super(start, end);
		}
		
		public void updateForDelete(Interval interval, List<Fold> newFolds) {
			if (interval.overlaps(this)) {
				if (children != null) {
					for (Fold f : children) {
						f.updateForDelete(interval, newFolds);
					}
				}
			} else {
				newFolds.add(this);
			}
		}

		public void move(int offset) {
			start += offset;
			end += offset;
			if (children != null) {
				for (Fold f : children) {
					f.move(offset);
				}
			}
		}

		public void setChildren(List<Fold> children) {
			this.children = children;
		}
		
		public List<Fold> getChildren() {
			return children;
		}
	}
}
