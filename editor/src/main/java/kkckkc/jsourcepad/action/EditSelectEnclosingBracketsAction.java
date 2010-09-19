package kkckkc.jsourcepad.action;

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
    public void actionPerformed(ActionEvent e) {
        Doc d = actionContext.get(ActionContextKeys.ACTIVE_DOC);
		Buffer buffer = d.getActiveBuffer();

        InsertionPoint insertionPoint = buffer.getInsertionPoint();

        List<List<String>> pairs = (List) Application.get().getBundleManager().getPreference(
                PrefKeys.PAIRS_SMART_TYPING, insertionPoint.getScope());

        Interval found = null;
        for (List<String> p : pairs) {
            char start = p.get(0).charAt(0);
            char end = p.get(1).charAt(0);

            Interval startMatch =
                    buffer.find(insertionPoint.getPosition(), Character.toString(start), Buffer.FindType.Literal, Buffer.Direction.Backward);
            if (startMatch == null) continue;

            Interval endMatch =
                    buffer.find(insertionPoint.getPosition(), Character.toString(end), Buffer.FindType.Literal, Buffer.Direction.Forward);
            if (endMatch == null) continue;

            Interval i = new Interval(startMatch.getStart(), endMatch.getEnd());

            if (found == null || i.getLength() < found.getLength()) {
                found = i;
            }
        }

        if (found != null) {
            buffer.setSelection(found);
        }
    }

}