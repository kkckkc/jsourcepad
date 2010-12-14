package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.Interval;

import java.awt.event.ActionEvent;

public class TextConvertToTitlecaseAction extends BaseAction {
    private final Window window;

	public TextConvertToTitlecaseAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void actionPerformed(ActionEvent e) {
		Buffer activeBuffer = window.getDocList().getActiveDoc().getActiveBuffer();

		Interval iv = activeBuffer.getSelectionOrCurrentLine();
        String text = activeBuffer.getText(iv);

        boolean inword = false;
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isLetter(chars[i])) {
                if (! inword) {
                    chars[i] = Character.toTitleCase(chars[i]);
                } else {
                    chars[i] = Character.toLowerCase(chars[i]);
                }
                inword = true;
            } else {
                inword = false;
            }
        }

		activeBuffer.replaceText(iv, new String(chars), null);
    }

}