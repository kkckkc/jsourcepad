package kkckkc.syntaxpane.model;

import com.google.common.collect.MapMaker;
import kkckkc.syntaxpane.model.LineManager.Line;
import kkckkc.syntaxpane.regex.Pattern;
import org.jetbrains.annotations.NotNull;

import java.util.*;



public class MutableFoldManager implements FoldManager {
    private static final int FLAG_FOLDABLE = (1 << 10);
    private static final int FLAG_FOLDABLE_END = (1 << 11);
    private static final int FLAG_FOLDED = (1 << 12);

    private final Object LOCK = new Object();

    private Map<Line, Fold> folds = new MapMaker().weakKeys().makeMap();
    private Set<Fold> outerFolds = new TreeSet<Fold>();

    private LineManager lineManager;
    private Fold cachedFold = null;

    private List<FoldListener> foldListeners = new ArrayList<FoldListener>();

    private Pattern foldEndPattern;
    private Pattern foldStartPattern;

    private TabManager tabManager;

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

        while (! isFoldable(line)) {
            line = lineManager.getPrevious(line);
            if (line == null) break;
        }

        return line;
    }

	@Override
    public void toggle(int idx) {
        Line line = lineManager.getLineByIdx(idx);
        if (line == null) return;
        Fold fold = folds.get(line);
        if (fold == null) return;

		if (fold.isFolded()) {
			unfold(fold);
		} else {
			fold(fold);
		}
	}

    private void fold(Fold fold) {
        boolean changed = !fold.isFolded();
        fold.setFolded(true);

        updateOuterFolds(fold);

        if (changed) fireFoldUpdated();
    }

    private void unfold(Fold fold) {
        boolean changed = fold.isFolded();
        fold.setFolded(false);

        updateOuterFolds(fold);

		if (changed) fireFoldUpdated();
    }

    @Override
    public void fold(int idx) {
        Line line = lineManager.getLineByIdx(idx);
        if (line == null) return;

        Fold fold = folds.get(line);
        if (fold == null) return;

        fold(fold);
    }

    @Override
    public void unfold(int idx) {
        Line line = lineManager.getLineByIdx(idx);
        if (line == null) return;

        Fold fold = folds.get(line);
        if (fold == null) return;

        unfold(fold);
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
        Line line = lineManager.getLineByIdx(idx);
        if (line == null) return State.DEFAULT;

        long flag = line.getFlags();
        if ((flag & FLAG_FOLDED) != 0) {
            if ((flag & FLAG_FOLDABLE) != 0) {
                return State.FOLDED_FIRST_LINE;
            } else {
                return State.FOLDED_SECOND_LINE_AND_REST;
            }
        } else {
            if ((flag & FLAG_FOLDABLE) != 0) {
                return State.FOLDABLE;
            } else if ((flag & FLAG_FOLDABLE_END) != 0) {
                return State.FOLDABLE_END;
            } else {
                return State.DEFAULT;
            }
        }
	}

    private boolean isFoldable(@NotNull Line line) {
        return folds.containsKey(line);
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

            Iterator<Line> it = lineManager.iterator(fold.getStart(), fold.getEnd());
            while (it.hasNext()) {
                Line l = it.next();

                l.clearFlag(FLAG_FOLDABLE);
                l.clearFlag(FLAG_FOLDED);

                for (Fold f : outerFolds) {
                    if (f.contains(l.getIdx())) {
                        l.addFlag(FLAG_FOLDED);
                        if (f.getStart() == l.getIdx()) {
                            l.addFlag(FLAG_FOLDABLE);
                        }
                        break;
                    }
                }

                if ((l.getFlags() & FLAG_FOLDED) == 0 && folds.containsKey(l)) {
                    l.addFlag(FLAG_FOLDABLE);
                }
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
        int offset = 0;
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
        int idx = line.getIdx();
        boolean change = false;
        if (b) {
            Fold old = folds.get(line);
            change = old == null || old.getLevel() != level;
            if (old == null) {
                synchronized (LOCK) {
                    folds.put(line, new Fold(idx, level));
                }
            } else {
                old.level = level;
            }
            line.addFlag(FLAG_FOLDABLE);
        } else {
            Fold old = folds.get(line);
            if (old != null) {
                if (old.isFolded()) {
                    unfold(idx);
                }
                synchronized (LOCK) {
                    folds.remove(line);
                }

                line.clearFlag(FLAG_FOLDABLE);

                if (old.getEnd() != old.getStart()) {
                    line = lineManager.getLineByIdx(old.getEnd());
                    if (line != null) {
                        line.clearFlag(FLAG_FOLDABLE_END);
                    }
                }


                change = true;
            } else if ((line.getFlags() & FLAG_FOLDABLE_END) != 0) {
                line.clearFlag(FLAG_FOLDABLE_END);
                change = true;
            }
        }
        return change;
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

    public void setTabManager(TabManager tabManager) {
        this.tabManager = tabManager;
    }

    public Interval getFold(Line line) {
        int start = line.getIdx();
        Fold fold = folds.get(line);
        while (fold == null || ! fold.contains(start)) {
            line = lineManager.getPrevious(line);
            if (line == null) break;
            fold = folds.get(line);
        }

        if (line == null) return null;
        return folds.get(line);
    }


    public interface FoldListener extends EventListener {
		public void foldUpdated();
	}


	public void linesRemoved(Line first, Line second) {
		if (first.equals(second)) return;
        if (folds.isEmpty()) return;

		synchronized (LOCK) {
			for (Fold fold : folds.values()) {
                if (fold.getStart() > second.getIdx()) {
					fold.move(-(second.getIdx() - first.getIdx()));
				}
			}
		}

        fireFoldUpdated();
	}

	public void linesAdded(Line first, Line second) {
		boolean foldsUpdated = false;

        synchronized (LOCK) {
            for (Fold fold : folds.values()) {
                if (fold.getStart() > first.getIdx()) {
                    fold.move(second.getIdx() - first.getIdx());
                    foldsUpdated = true;
                }
            }
        }

        processText(first, second);
		
        if (foldsUpdated) fireFoldUpdated();
	}
	
    public void linesUpdated(Line first, Line second) {
        boolean foldsUpdated = processText(first, second);
        if (foldsUpdated) fireFoldUpdated();
    }

    private boolean processText(Line first, Line second) {
        if (foldStartPattern == null) return false;

        boolean foldChanges = false;

        Line line = first;

        Stack<Fold> foldStack = new Stack<Fold>();

        int level = getLevel(first);
        while (line != null) {
            boolean change = false;
            CharSequence lt = line.getCharSequence(false);
            if (foldStartPattern.matcher(lt).matches()) {
                change = setFoldableFlag(line, true, ++level);
                Fold fold = folds.get(line);
                foldStack.push(fold);
            } else if (foldEndPattern.matcher(lt).matches()) {
                boolean found = false;
                line.addFlag(FLAG_FOLDABLE_END);
                String prefix = getPrefix(line);
                while (! foldStack.isEmpty()) {
                    Fold p = foldStack.pop();
                    if (found && ! prefix.equals(getPrefix(lineManager.getLineByIdx(p.start)))) {
                        foldStack.push(p);
                        break;
                    }
                    p.setEnd(line.getIdx());
                    if (prefix.equals(getPrefix(lineManager.getLineByIdx(p.start)))) {
                        found = true;
                    }
                    level--;
                }
                change = true;
            } else {
                change = setFoldableFlag(line, false, level);
            }

            if (! change && line.getIdx() > second.getIdx()) break;

            foldChanges |= change;

            line = lineManager.getNext(line);
        }

        return foldChanges;
    }

    private String getPrefix(Line line) {
        String s = line.getCharSequence(false).toString();
        if (this.tabManager == null) {
            int i = 0;
            while (Character.isWhitespace(s.charAt(i))) {
                i++;
            }
            return s.substring(0, i);
        } else {
            return tabManager.createIndent(tabManager.getTabCount(s));
        }
    }

    private int getLevel(Line line) {
        while (line != null && ! folds.containsKey(line)) {
            line = lineManager.getPrevious(line);
        }

        if (line == null) return 0;
        Fold f = folds.get(line);
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


    public String toString() {
        StringBuilder b = new StringBuilder();
        for (Line line : lineManager) {
            b.append(line).
                    append(String.format("%-25s", getFoldState(line.getIdx()))).append(" ").
                    append(((line.getFlags() & FLAG_FOLDABLE) != 0) ? "v" : " ").append(" ").
                    append(((line.getFlags() & FLAG_FOLDABLE_END) != 0) ? "^" : " ").append(" ").
                    append(((line.getFlags() & FLAG_FOLDED) != 0) ? "-" : " ").append(" ").
                    append(folds.get(line)).append("\n");
        }
        b.append(outerFolds).append("\n");
        //b.append(folds);
        return b.toString();
    }
}
