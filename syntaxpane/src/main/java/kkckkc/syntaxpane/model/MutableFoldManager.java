package kkckkc.syntaxpane.model;

import kkckkc.syntaxpane.model.LineManager.Line;
import kkckkc.syntaxpane.regex.Pattern;

import java.util.*;



public class MutableFoldManager implements FoldManager {
    private final Object LOCK = new Object();

    private LineManager lineManager;
    private Fold cachedFold = null;

    private TreeMap<Integer, Fold> foldables = new TreeMap<Integer, Fold>();
	private Set<Fold> outerFolds = new HashSet<Fold>();
    private List<FoldListener> foldListeners = new ArrayList<FoldListener>();

    private Pattern foldEndPattern;
    private Pattern foldStartPattern;

    public MutableFoldManager(LineManager lineManager) {
		this.lineManager = lineManager;
	}
	
	public void setFoldEndPattern(Pattern foldEndPattern) {
		this.foldEndPattern = foldEndPattern;
	}

    public void setFoldStartPattern(Pattern foldStartPattern) {
        this.foldStartPattern = foldStartPattern;
    }

    @Override
    public Line getClosestFoldStart(int position) {
        Line line = lineManager.getLineByPosition(position);

        while (position >= 0 && ! isFoldable(position)) {
            position--;
        }

        return position >= 0 ? lineManager.getLineByPosition(position) : line;
    }

	@Override
    public void toggle(int line) {
        Fold fold = foldables.get(line);
        if (fold == null) return;

		if (fold.isFolded()) {
			unfold(line);
		} else {
			fold(line);
		}
	}

	@Override
    public Fold getBiggestFoldedSectionOverlapping(int id) {
        if (cachedFold != null && cachedFold.contains(id)) {
            return cachedFold;
        }

        for (Fold fold : outerFolds) {
            if (fold.contains(id)) {
                cachedFold = fold;
                return fold;
            }
        }

        return null;
	}
	
	@Override
    public State getFoldState(int idx) {
		Fold fold = getBiggestFoldedSectionOverlapping(idx);
        if (fold != null) {
            if (fold.getStart() == idx) {
                return State.FOLDED_FIRST_LINE;
            } else {
                return State.FOLDED_SECOND_LINE_AND_REST;
            }
        } else {
            if (isFoldable(idx)) {
                return State.FOLDABLE;
            } else {
                return State.DEFAULT;
            }
        }
	}
	
	@Override
    public void fold(int line) {
        Fold fold = foldables.get(line);
        if (fold == null) return;

        boolean changed = !fold.isFolded();
        fold.setFolded(true);

        updateOuterFolds(fold);

		if (changed) fireFoldUpdated();
	}

    private boolean isFoldable(int line) {
        return foldables.containsKey(line);
    }

    @Override
    public void unfold(int line) {
        Fold fold = foldables.get(line);
        if (fold == null) return;

        boolean changed = fold.isFolded();
        fold.setFolded(false);

        updateOuterFolds(fold);

		if (changed) fireFoldUpdated();
	}

    private void updateOuterFolds(Fold fold) {
        synchronized (LOCK) {
            Fold parentFold = null;
            for (Fold f : outerFolds) {
                if (f.contains(fold)) {
                    parentFold = f;
                }
            }

            if (parentFold != null) {
                if (fold.isFolded()) {
                    parentFold.getChildren().add(fold);
                } else {
                    parentFold.getChildren().remove(fold);
                }
            }

            if (fold.isFolded()) {
                Iterator<Fold> it = outerFolds.iterator();
                while (it.hasNext()) {
                    Fold f = it.next();
                    if (fold.contains(f)) {
                        it.remove();
                        fold.getChildren().add(f);
                    }
                }

                outerFolds.add(fold);
            } else {
                for (Fold f : fold.getChildren()) {
                    outerFolds.add(f);
                }

                outerFolds.remove(fold);
            }
        }
    }

    public int toVisibleIndex(int line) {
		int offset = 0;
        for (Fold fold : outerFolds) {
            if (fold.getEnd() < line) {
				offset += fold.getLength();
			}
		}
		return line - offset;
	}

	public int fromVisibleIndex(int line) {
        for (Fold fold : outerFolds) {
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
        for (Fold fold : outerFolds) {
            lineCount -= fold.getLength();
		}
		return lineCount;
	}

	public int getLineCount() {
		return lineManager.size();
	}

	private boolean setFoldableFlag(Line line, boolean b, int level) {
        synchronized (LOCK) {
            int idx = line.getIdx();
            boolean change = false;
            if (b) {
                Fold old = foldables.put(idx, new Fold(idx, level));
                change = old == null || old.getLevel() != level;
            } else {
                Fold old = foldables.get(idx);
                if (old != null) {
                    if (old.isFolded()) {
                        unfold(idx);
                    }
                    foldables.remove(idx);
                    change = true;
                }
            }
            return change;
        }
	}

	public void fireFoldUpdated() {
        cachedFold = null;
		for (FoldListener listener : foldListeners) {
			listener.foldUpdated();
		}
	}
	
	public void addFoldListener(FoldListener foldListener) {
		this.foldListeners.add(foldListener);
	}


    public interface FoldListener extends EventListener {
		public void foldUpdated();
	}


	public void linesRemoved(Interval interval) {
		if (interval.isEmpty()) return;
        if (foldables.isEmpty()) return;

		TreeMap<Integer, Fold> newFolds = new TreeMap<Integer, Fold>();
			
		synchronized (LOCK) {
			for (Fold fold : foldables.values()) {
                if (fold.getStart() > interval.getEnd()) {
					fold.move(- interval.getLength());
					newFolds.put(fold.start, fold);
				} else {
					newFolds.put(fold.start, fold);
				}
			}
			
            foldables = newFolds;
		}

        fireFoldUpdated();
	}

	public void linesAdded(Interval interval) {
		boolean foldsUpdated = false;

        TreeMap<Integer, Fold> newFolds = new TreeMap<Integer, Fold>();

        synchronized (LOCK) {
            for (Fold fold : foldables.values()) {
                if (fold.getStart() > interval.getStart()) {
                    fold.move(interval.getLength());
                    foldsUpdated = true;
                    newFolds.put(fold.start, fold);
                } else {
					newFolds.put(fold.start, fold);
				}
            }

            foldables = newFolds;
        }

        processText(interval);
		
        if (foldsUpdated) fireFoldUpdated();
	}
	
    public void linesUpdated(Interval interval) {
        boolean foldsUpdated = processText(interval);
        if (foldsUpdated) fireFoldUpdated();
    }

    private boolean processText(Interval interval) {
        if (foldStartPattern == null) return false;

        boolean foldChanges = false;

        Line line = lineManager.getLineByIdx(interval.getStart());

        Stack<Fold> foldStack = new Stack<Fold>();

        int level = getLevel(interval.getStart());
        while (line != null) {
            boolean change = false;
            CharSequence lt = line.getCharSequence(false);
            if (foldStartPattern.matcher(lt).matches()) {
                change = setFoldableFlag(line, true, ++level);
                Fold fold = foldables.get(line.getIdx());
                foldStack.push(fold);
            } else if (foldEndPattern.matcher(lt).matches()) {
                String prefix = getPrefix(line);
                while (! foldStack.isEmpty()) {
                    Fold p = foldStack.pop();
                    p.setEnd(line.getIdx());
                    if (prefix.equals(getPrefix(lineManager.getLineByIdx(p.start)))) {
                        break;
                    }
                    level--;
                }
            } else {
                change = setFoldableFlag(line, false, level);
            }

            if (! change && line.getIdx() > interval.getEnd()) break;

            foldChanges |= change;

            line = lineManager.getNext(line);
        }

        return foldChanges;
    }

    private String getPrefix(Line line) {
        String s = line.getCharSequence(false).toString();
        int i = 0;
        while (Character.isWhitespace(s.charAt(i))) {
            i++;
        }
        return s.substring(0, i);
    }

    private int getLevel(int line) {
        while (line >= 0 && ! foldables.containsKey(line)) {
            line--;
        }

        Fold f = foldables.get(line);
        if (f == null) return 0;
        return f.getLevel();
    }



    static class Fold extends Interval {
        private boolean folded;
        private int level;
        private List<Fold> children;

        Fold(int start, int level) {
            super(start, start);
            this.setLevel(level);
        }

        public void move(int offset) {
            start += offset;
            end += offset;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public boolean isFolded() {
            return folded;
        }

        public void setFolded(boolean folded) {
            this.folded = folded;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public List<Fold> getChildren() {
            if (children == null) children = new ArrayList<Fold>();
            return children;
        }
    }
}
