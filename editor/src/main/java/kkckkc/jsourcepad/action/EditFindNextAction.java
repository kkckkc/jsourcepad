package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;
import kkckkc.jsourcepad.model.Buffer;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.Interval;

public class EditFindNextAction extends BaseAction {
    private final Window window;

	public EditFindNextAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();

        int position = buffer.getInsertionPoint().getPosition();
        Interval selection = buffer.getSelection();
        if (selection != null) {
            position = selection.getEnd();
        }

        buffer.getFinder().forward(position);
    }

}
