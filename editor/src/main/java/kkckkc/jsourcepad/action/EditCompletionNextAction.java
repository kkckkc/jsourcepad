package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class EditCompletionNextAction extends BaseAction {
	public EditCompletionNextAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void actionPerformed(ActionEvent e) {
		Buffer buffer = actionContext.get(ActionContextKeys.ACTIVE_DOC).getActiveBuffer();
        buffer.getCompletionManager().completeNext();
    }

}