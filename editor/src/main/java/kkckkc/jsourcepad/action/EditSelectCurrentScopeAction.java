package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.InsertionPoint;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.syntaxpane.model.LineManager;
import kkckkc.syntaxpane.model.Scope;

import java.awt.event.ActionEvent;

public class EditSelectCurrentScopeAction extends BaseAction {
    private final Window window;

	public EditSelectCurrentScopeAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void actionPerformed(ActionEvent e) {
		Doc d = window.getDocList().getActiveDoc();
		Buffer buffer = d.getActiveBuffer();

        InsertionPoint insertionPoint = buffer.getInsertionPoint();

        Scope scope = insertionPoint.getScope();

        LineManager.Line line = buffer.getLineManager().getLineByPosition(insertionPoint.getPosition());

        buffer.setSelection(new Interval(line.getStart() + scope.getStart(), line.getStart() + scope.getEnd()));
    }

}