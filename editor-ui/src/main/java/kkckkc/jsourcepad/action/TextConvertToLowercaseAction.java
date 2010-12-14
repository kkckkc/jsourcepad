package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.Interval;

import java.awt.event.ActionEvent;

public class TextConvertToLowercaseAction extends BaseAction {
    private final Window window;

	public TextConvertToLowercaseAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void actionPerformed(ActionEvent e) {
		Buffer activeBuffer = window.getDocList().getActiveDoc().getActiveBuffer();

		Interval selectionOrCurrentLine = activeBuffer.getSelectionOrCurrentLine();
        String text = activeBuffer.getText(selectionOrCurrentLine);

		activeBuffer.replaceText(selectionOrCurrentLine, text.toLowerCase(), null);
    }

}