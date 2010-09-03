package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;
import kkckkc.jsourcepad.model.Buffer;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.DocPresenter;
import kkckkc.jsourcepad.ui.dialog.find.FindDialog;
import kkckkc.jsourcepad.ui.dialog.find.FindDialogView;
import kkckkc.syntaxpane.model.Interval;

public class EditFindPreviousAction extends AbstractEditorAction {

	public EditFindPreviousAction(Window window) {
		super(window);
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
