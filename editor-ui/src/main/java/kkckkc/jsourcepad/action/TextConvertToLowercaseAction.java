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
		Buffer b = window.getDocList().getActiveDoc().getActiveBuffer();

		Interval i = b.getSelectionOrCurrentLine();
        String text = b.getText(i);

		b.replaceText(i, text.toLowerCase(), null);
    }

}