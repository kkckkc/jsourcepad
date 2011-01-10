package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class EditRedoAction extends BaseAction {
	public EditRedoAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC, ActionStateRules.CAN_REDO);
	}
	
	@Override
    public void performAction(ActionEvent e) {
        Doc activeDoc = actionContext.get(ActionContextKeys.ACTIVE_DOC);
		activeDoc.getActiveBuffer().redo();
    }

}
