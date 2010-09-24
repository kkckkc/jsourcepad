package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class EditFindJumpToSelectionAction extends BaseAction {
	public EditFindJumpToSelectionAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC, ActionStateRules.TEXT_SELECTED);
	}

    @Override
    public void actionPerformed(ActionEvent e) {
        Doc d = actionContext.get(ActionContextKeys.ACTIVE_DOC);
        Buffer buffer = d.getActiveBuffer();
        buffer.scrollTo(buffer.getSelection().getStart(), Buffer.ScrollAlignment.MIDDLE);
    }

}