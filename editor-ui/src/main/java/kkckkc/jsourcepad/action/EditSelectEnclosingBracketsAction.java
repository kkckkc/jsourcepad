package kkckkc.jsourcepad.action;

import com.google.common.base.Function;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.InsertionPoint;
import kkckkc.jsourcepad.model.bundle.PrefKeys;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.Interval;

import java.awt.event.ActionEvent;
import java.util.List;

public class EditSelectEnclosingBracketsAction extends BaseAction {
	public EditSelectEnclosingBracketsAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void performAction(ActionEvent e) {
        Doc activeDoc = actionContext.get(ActionContextKeys.ACTIVE_DOC);
		Buffer buffer = activeDoc.getActiveBuffer();

        InsertionPoint insertionPoint = buffer.getInsertionPoint();

        List<List<String>> pairs = (List) Application.get().getBundleManager().getPreference(
                PrefKeys.PAIRS_SMART_TYPING, insertionPoint.getScope());

        Interval found = null;
        for (List<String> p : pairs) {
            char start = p.get(0).charAt(0);
            char end = p.get(1).charAt(0);

            class Finder implements Function<String, Interval> {
                private char c;
                private Buffer.Direction direction;

                Finder(char c, Buffer.Direction direction) {
                    this.c = c;
                    this.direction = direction;
                }

                @Override
                public Interval apply(String s) {
                    if (direction == Buffer.Direction.Backward) {
                        int p = s.lastIndexOf(c);
                        if (p < 0) return null;
                        return Interval.createWithLength(p, 1);
                    } else {
                        int p = s.indexOf(c);
                        if (p < 0) return null;
                        return Interval.createWithLength(p, 1);
                    }
                }
            }

            Interval startMatch =
                    buffer.processCharacters(insertionPoint.getPosition(), new Finder(start, Buffer.Direction.Backward), Buffer.Direction.Backward);
            if (startMatch == null) continue;

            Interval endMatch =
                    buffer.processCharacters(insertionPoint.getPosition(), new Finder(end, Buffer.Direction.Forward), Buffer.Direction.Forward);
            if (endMatch == null) continue;

            Interval interval = new Interval(startMatch.getStart(), endMatch.getEnd());

            if (found == null || interval.getLength() < found.getLength()) {
                found = interval;
            }
        }

        if (found != null) {
            buffer.setSelection(found);
        }
    }

}