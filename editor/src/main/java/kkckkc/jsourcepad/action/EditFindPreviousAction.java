package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;
import kkckkc.jsourcepad.model.Buffer;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.DocPresenter;
import kkckkc.jsourcepad.ui.dialog.find.FindDialog;
import kkckkc.jsourcepad.ui.dialog.find.FindDialogView;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.Interval;

public class EditFindPreviousAction extends BaseAction {
    private final Window window;

	public EditFindPreviousAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();

        int position = buffer.getInsertionPoint().getPosition();
        Interval selection = buffer.getSelection();
        if (selection != null) {
            position = selection.getStart();
        }

        buffer.getFinder().backward(position);
    }

}
