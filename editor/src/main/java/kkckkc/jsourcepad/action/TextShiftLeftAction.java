package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.syntaxpane.model.Interval;

public class TextShiftLeftAction extends AbstractEditorAction {

	public TextShiftLeftAction(Window window) {
		super(window);
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		Buffer b = window.getDocList().getActiveDoc().getActiveBuffer();
		Interval i = b.getSelectionOrCurrentLine();
		b.shift(i, -1);
    }

}
