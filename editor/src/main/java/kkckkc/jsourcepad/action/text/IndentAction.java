package kkckkc.jsourcepad.action.text;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.InsertionPoint;
import kkckkc.syntaxpane.model.Interval;

public final class IndentAction extends AbstractAction {
    private final Doc doc;

    public IndentAction(Doc doc) {
	    this.doc = doc;
    }

    public void actionPerformed(ActionEvent e) {
    	InsertionPoint ip = doc.getActiveBuffer().getInsertionPoint();
    	doc.getActiveBuffer().indent(Interval.createEmpty(ip.getPosition()));
    }
}