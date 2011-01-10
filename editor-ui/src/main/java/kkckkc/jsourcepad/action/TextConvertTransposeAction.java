package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.Interval;

import java.awt.event.ActionEvent;

public class TextConvertTransposeAction extends BaseAction {
    private final Window window;

	public TextConvertTransposeAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void performAction(ActionEvent e) {
        Buffer activeBuffer = window.getDocList().getActiveDoc().getActiveBuffer();

        Interval iv = activeBuffer.getSelectionOrCurrentLine();
        String text = activeBuffer.getText(iv);

        char[] chars = text.toCharArray();
        char[] dest = new char[chars.length]; 
        for (int i = 0; i < chars.length; i++) {
            dest[chars.length - 1 - i] = chars[i];
        }

        activeBuffer.replaceText(iv, new String(dest), null);
    }

}