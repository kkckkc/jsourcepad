package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class EditFindReplaceAndFindAction extends BaseAction {
	public EditFindReplaceAndFindAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC, ActionStateRules.HAS_ACTIVE_FIND);
	}

    @Override
    public void actionPerformed(ActionEvent e) {
        Doc activeDoc = actionContext.get(ActionContextKeys.ACTIVE_DOC);
		Buffer buffer = activeDoc.getActiveBuffer();

        buffer.getFinder().replace();
        buffer.getFinder().forward(buffer.getInsertionPoint().getPosition());
    }

}