package kkckkc.jsourcepad.action.text;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.InsertionPoint;
import kkckkc.jsourcepad.model.bundle.PrefKeys;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.Interval;

import java.awt.event.ActionEvent;
import java.util.List;

public final class NewlineAction extends BaseAction {
    public void performAction(ActionEvent e) {
        Doc doc = Application.get().getWindowManager().getFocusedWindow().getDocList().getActiveDoc();

    	InsertionPoint ip = doc.getActiveBuffer().getInsertionPoint();

        Buffer buffer = doc.getActiveBuffer();
        InsertionPoint insertionPoint = buffer.getInsertionPoint();

        boolean inSmartTypingPair = false;

        if (ip.getPosition() > 0) {
            List<List<String>> pairs = (List) Application.get().getBundleManager().getPreference(
                    PrefKeys.PAIRS_SMART_TYPING, insertionPoint.getScope());

            String charToTheRight = buffer.getText(Interval.createWithLength(ip.getPosition(), 1));
            String charToTheLeft = buffer.getText(Interval.createWithLength(ip.getPosition() - 1, 1));

            for (List<String> pair : pairs) {
                if (pair.get(0).equals(charToTheLeft) && pair.get(1).equals(charToTheRight)) {
                    inSmartTypingPair = true;
                }
            }
        }

        if (inSmartTypingPair) {
            buffer.insertText(ip.getPosition(), "\n\n", null);
            buffer.indent(Interval.createWithLength(ip.getPosition(), 2));
            buffer.setSelection(Interval.createEmpty(buffer.getInsertionPoint().getPosition() - 1));
        } else {
            buffer.insertText(ip.getPosition(), "\n", null);
            buffer.indent(Interval.createEmpty(ip.getPosition() + 1));
        }
    }
}