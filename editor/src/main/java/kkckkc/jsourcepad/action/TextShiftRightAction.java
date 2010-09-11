package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.Interval;

public class TextShiftRightAction extends BaseAction {
    private final Window window;

	public TextShiftRightAction(Window window) {
		this.window = window;
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		Buffer b = window.getDocList().getActiveDoc().getActiveBuffer();
		Interval i = b.getSelectionOrCurrentLine();
		b.shift(i, 1);
    }

}
