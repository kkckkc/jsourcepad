package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.Interval;

import java.awt.event.ActionEvent;

public class TextConvertToOppositeCaseAction extends BaseAction {
    private final Window window;

	public TextConvertToOppositeCaseAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void actionPerformed(ActionEvent e) {
        Buffer activeBuffer = window.getDocList().getActiveDoc().getActiveBuffer();

        Interval iv = activeBuffer.getSelectionOrCurrentLine();
        String text = activeBuffer.getText(iv);

        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isLetter(chars[i])) {
                if (Character.isUpperCase(chars[i])) {
                    chars[i] = Character.toLowerCase(chars[i]);
                } else {
                    chars[i] = Character.toUpperCase(chars[i]);
                }
            }
        }

        activeBuffer.replaceText(iv, new String(chars), null);
    }

}