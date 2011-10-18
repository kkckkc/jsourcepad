package kkckkc.syntaxpane.model;

public interface FoldManager {
    Interval getFoldedSectionOverlapping(int line);
    Interval getFold(LineManager.Line line);
    State getFoldState(LineManager.Line line);

    void fold(LineManager.Line line);
    void unfold(LineManager.Line line);
    void toggle(LineManager.Line line);


    public enum State { DEFAULT, FOLDABLE, FOLDABLE_END, FOLDED_FIRST_LINE, FOLDED_SECOND_LINE_AND_REST }
}
