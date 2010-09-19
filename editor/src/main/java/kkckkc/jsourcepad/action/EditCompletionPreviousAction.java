package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class EditCompletionPreviousAction extends BaseAction {
	public EditCompletionPreviousAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void actionPerformed(ActionEvent e) {
        Buffer buffer = actionContext.get(ActionContextKeys.ACTIVE_DOC).getActiveBuffer();
        buffer.getCompletionManager().completePrevious();
    }

}