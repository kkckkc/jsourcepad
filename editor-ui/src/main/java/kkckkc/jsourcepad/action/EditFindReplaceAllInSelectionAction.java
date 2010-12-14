package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class EditFindReplaceAllInSelectionAction extends BaseAction {
	public EditFindReplaceAllInSelectionAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC, ActionStateRules.HAS_ACTIVE_FIND, ActionStateRules.TEXT_SELECTED);
	}

    @Override
    public void actionPerformed(ActionEvent e) {
        Doc activeDoc = actionContext.get(ActionContextKeys.ACTIVE_DOC);
		Buffer buffer = activeDoc.getActiveBuffer();

        buffer.getFinder().replaceAll(buffer.getSelection());
    }

}