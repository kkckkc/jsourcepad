package kkckkc.syntaxpane.model;

public interface FoldManager {
    LineManager.Line getClosestFoldStart(int idx);

    void toggle(LineManager.Line line);

    MutableFoldManager.Fold getFoldStartingWith(int id);

    Interval getFoldOverlapping(int id);

    State getFoldState(int idx);

    void fold(LineManager.Line line);

    void unfold(int line);

    public enum State { DEFAULT, FOLDABLE, FOLDED_FIRST_LINE, FOLDED_SECOND_LINE_AND_REST }
}
