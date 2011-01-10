package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class EditFindReplaceAllAction extends BaseAction {
	public EditFindReplaceAllAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC, ActionStateRules.HAS_ACTIVE_FIND);
	}

    @Override
    public void performAction(ActionEvent e) {
        Doc activeDoc = actionContext.get(ActionContextKeys.ACTIVE_DOC);
        Buffer buffer = activeDoc.getActiveBuffer();

        buffer.getFinder().replaceAll(buffer.getCompleteDocument());
    }

}