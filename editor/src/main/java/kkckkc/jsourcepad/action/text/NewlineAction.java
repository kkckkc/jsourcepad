package kkckkc.jsourcepad.action.text;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.InsertionPoint;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.Interval;

import java.awt.event.ActionEvent;

public final class NewlineAction extends BaseAction {
    public void performAction(ActionEvent e) {
        Doc doc = Application.get().getWindowManager().getFocusedWindow().getDocList().getActiveDoc();

    	InsertionPoint ip = doc.getActiveBuffer().getInsertionPoint();
        doc.getActiveBuffer().insertText(ip.getPosition(), "\n", null);
    	doc.getActiveBuffer().indent(Interval.createEmpty(ip.getPosition()));
    }
}