package kkckkc.syntaxpane.model;

public interface FoldManager {
    LineManager.Line getClosestFoldStart(int line);
    Interval getBiggestFoldedSectionOverlapping(int line);

    void fold(int line);
    void unfold(int line);
    void toggle(int line);

    State getFoldState(int line);
    public enum State { DEFAULT, FOLDABLE, FOLDABLE_END, FOLDED_FIRST_LINE, FOLDED_SECOND_LINE_AND_REST }


    // level,isstart
}
