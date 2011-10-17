package com.github.kkckkc.syntaxpane.parse;

import junit.framework.TestCase;
import kkckkc.syntaxpane.model.FoldManager;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.syntaxpane.model.LineManager.Line;
import kkckkc.syntaxpane.model.MutableFoldManager;
import kkckkc.syntaxpane.model.MutableLineManager;
import kkckkc.syntaxpane.parse.CharProvider;
import kkckkc.syntaxpane.regex.LiteralPatternFactory;
import kkckkc.utils.Pair;

public class MutableFoldManagerTest extends TestCase {
    private MutableLineManager lineManager;
    private MutableFoldManager foldManager;
    private StringBuffer text;

    public void testSimple() {
        assertFoldState(foldManager,
                FoldManager.State.FOLDABLE, FoldManager.State.DEFAULT, FoldManager.State.FOLDABLE,
                FoldManager.State.DEFAULT, FoldManager.State.FOLDABLE_END, FoldManager.State.FOLDABLE_END);


        foldManager.toggle(2);

        assertFoldState(foldManager,
                FoldManager.State.FOLDABLE, FoldManager.State.DEFAULT, FoldManager.State.FOLDED_FIRST_LINE,
                FoldManager.State.FOLDED_SECOND_LINE_AND_REST, FoldManager.State.FOLDED_SECOND_LINE_AND_REST, FoldManager.State.FOLDABLE_END);


        foldManager.toggle(0);

        assertFoldState(foldManager,
                FoldManager.State.FOLDED_FIRST_LINE, FoldManager.State.FOLDED_SECOND_LINE_AND_REST, FoldManager.State.FOLDED_SECOND_LINE_AND_REST,
                FoldManager.State.FOLDED_SECOND_LINE_AND_REST, FoldManager.State.FOLDED_SECOND_LINE_AND_REST, FoldManager.State.FOLDED_SECOND_LINE_AND_REST);


        foldManager.toggle(0);

        assertFoldState(foldManager,
                FoldManager.State.FOLDABLE, FoldManager.State.DEFAULT, FoldManager.State.FOLDED_FIRST_LINE,
                FoldManager.State.FOLDED_SECOND_LINE_AND_REST, FoldManager.State.FOLDED_SECOND_LINE_AND_REST, FoldManager.State.FOLDABLE_END);
	}

    @Override
    protected void setUp() throws Exception {
        text = new StringBuffer(
            "{\n" +                         //0
            "  Lorem\n" +                   //1
            "  {\n" +                       //2
            "    Ipsum\n" +                 //3
            "  }\n" +                       //4
            "}");                           //5
        CharProvider p = new CharProvider.StringBuffer(text);

        lineManager = new MutableLineManager(p);

        foldManager = new MutableFoldManager(lineManager);
        foldManager.setFoldStartPattern(new LiteralPatternFactory().create("{"));
        foldManager.setFoldEndPattern(new LiteralPatternFactory().create("}"));

        lineManager.intervalAdded(new Interval(0, text.length()));

        Line l = lineManager.getLineByIdx(5);
        foldManager.linesAdded(lineManager.getLineByIdx(0), l);
    }

    public void testAddLines() {
        text.insert(2, "B\n");
        Pair<Line, Line> pair = lineManager.intervalAdded(new Interval(2, 4));
        foldManager.linesAdded(pair.getFirst(), pair.getSecond());

        assertFoldState(foldManager,
                FoldManager.State.FOLDABLE, FoldManager.State.DEFAULT, FoldManager.State.DEFAULT, FoldManager.State.FOLDABLE,
                FoldManager.State.DEFAULT, FoldManager.State.FOLDABLE_END, FoldManager.State.FOLDABLE_END);

        System.out.println(foldManager);
    }

    private void assertFoldState(MutableFoldManager foldManager, FoldManager.State... states) {
        int i = 0;
        for (FoldManager.State state : states) {
            assertEquals(state, foldManager.getFoldState(i));
            i++;
        }
    }
}
