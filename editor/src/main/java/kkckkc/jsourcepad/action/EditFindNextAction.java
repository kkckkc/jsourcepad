package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.Interval;

import java.awt.event.ActionEvent;

public class EditFindNextAction extends BaseAction {
	public EditFindNextAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC, ActionStateRules.HAS_ACTIVE_FIND);
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
        Doc d = actionContext.get(ActionContextKeys.ACTIVE_DOC);
		Buffer buffer = d.getActiveBuffer();

        int position = buffer.getInsertionPoint().getPosition();
        Interval selection = buffer.getSelection();
        if (selection != null) {
            position = selection.getEnd();
        }

        buffer.getFinder().forward(position);
    }

}
