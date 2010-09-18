package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class EditFindJumpToSelectionAction extends BaseAction {
    private final Window window;

	public EditFindJumpToSelectionAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC, ActionStateRules.TEXT_SELECTED);
	}

    @Override
    public void actionPerformed(ActionEvent e) {
        Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();
        buffer.scrollTo(buffer.getSelection().getStart());
    }

}