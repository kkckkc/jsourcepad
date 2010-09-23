package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.Interval;

import java.awt.event.ActionEvent;

public class TextUnwrapSelectionAction extends BaseAction {
    private final Window window;

	public TextUnwrapSelectionAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void actionPerformed(ActionEvent e) {
		Buffer b = window.getDocList().getActiveDoc().getActiveBuffer();

        Interval i = b.getSelectionOrCurrentParagraph();
        String text = b.getText(i);

        text = text.replaceAll("\n", "");

		b.replaceText(i, text, null);
    }


    @Override
    protected void actionContextUpdated() {
        if (ActionStateRules.TEXT_SELECTED.shouldBeEnabled(actionContext)) {
            putValue(NAME, "Unwrap Selection");
        } else {
            putValue(NAME, "Unwrap Paragraph");
        }
    }

}