package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class EditUndoAction extends BaseAction {
	public EditUndoAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC, ActionStateRules.CAN_UNDO);
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		Doc d = actionContext.get(ActionContextKeys.ACTIVE_DOC);
		d.getActiveBuffer().undo();
    }

}
