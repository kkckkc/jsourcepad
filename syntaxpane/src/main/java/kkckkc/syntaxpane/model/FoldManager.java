package kkckkc.syntaxpane.model;

/**
 * Created by IntelliJ IDEA.
 * User: kkckkc
 * Date: 2010-sep-21
 * Time: 20:24:26
 * To change this template use File | Settings | File Templates.
 */
public interface FoldManager {
    LineManager.Line getClosestFoldableLine(int idx);

    void toggle(LineManager.Line line);

    MutableFoldManager.Fold getFoldStartingWith(int id);

    Interval getFoldOverlapping(int id);

    State getFoldState(int idx);

    void fold(LineManager.Line line);

    void unfold(int line);

    public enum State { DEFAULT, FOLDABLE, FOLDED_FIRST_LINE, FOLDED_SECOND_LINE_AND_REST }
}
