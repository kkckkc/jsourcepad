package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.StyleSettings;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.StringUtils;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.Interval;

import java.awt.event.ActionEvent;

public class TextReformatParagraphAction extends BaseAction {
    private final Window window;

	public TextReformatParagraphAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void actionPerformed(ActionEvent e) {
        StyleSettings ss = Application.get().getSettingsManager().get(StyleSettings.class);

		Buffer b = window.getDocList().getActiveDoc().getActiveBuffer();

        Interval i = b.getSelectionOrCurrentParagraph();
        String text = b.getText(i);

        String[] lines = StringUtils.wrap(text, ss.getWrapColumn());

        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            builder.append(line).append("\n");
        }

        if (! text.endsWith("\n"))
            builder.setLength(builder.length() - 1);

		b.replaceText(i, builder.toString(), null);
    }

    @Override
    protected void actionContextUpdated() {
        if (ActionStateRules.TEXT_SELECTED.shouldBeEnabled(actionContext)) {
            putValue(NAME, "Reformat Selection");
        } else {
            putValue(NAME, "Reformat Paragraph");
        }
    }
}