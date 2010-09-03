package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;
import kkckkc.jsourcepad.model.Buffer;

import kkckkc.jsourcepad.model.Window;
import kkckkc.syntaxpane.model.Interval;

public class EditFindNextAction extends AbstractEditorAction {

	public EditFindNextAction(Window window) {
		super(window);
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
