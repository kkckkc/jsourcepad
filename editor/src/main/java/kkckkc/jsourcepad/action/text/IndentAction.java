package kkckkc.jsourcepad.action.text;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.InsertionPoint;
import kkckkc.syntaxpane.model.Interval;

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class IndentAction extends AbstractAction {
    public void actionPerformed(ActionEvent e) {
        Doc doc = Application.get().getWindowManager().getFocusedWindow().getDocList().getActiveDoc();

    	InsertionPoint ip = doc.getActiveBuffer().getInsertionPoint();
    	doc.getActiveBuffer().indent(Interval.createEmpty(ip.getPosition()));
    }
}