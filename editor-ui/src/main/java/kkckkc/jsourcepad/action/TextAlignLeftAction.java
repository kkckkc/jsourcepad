package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.settings.StyleSettings;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.TextInterval;

import java.awt.event.ActionEvent;
import java.util.StringTokenizer;

public class TextAlignLeftAction extends BaseAction {
    private final Window window;

	public TextAlignLeftAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void actionPerformed(ActionEvent e) {
        StyleSettings ss = Application.get().getSettingsManager().get(StyleSettings.class);

		Buffer activeBuffer = window.getDocList().getActiveDoc().getActiveBuffer();

		TextInterval selectionOrCurrentLine = activeBuffer.getSelectionOrCurrentLine();
        String text = selectionOrCurrentLine.getText();

        StringBuilder builder = new StringBuilder();
        StringTokenizer tok = new StringTokenizer(text, "\n", true);
        while (tok.hasMoreTokens()) {
            String line = tok.nextToken();
            if (line.equals("\n")) builder.append(line);
            left(line, builder, ss.getWrapColumn());
        }

		activeBuffer.replaceText(selectionOrCurrentLine, builder.toString(), null);
    }

    private void left(String line, StringBuilder builder, int wrapColumn) {
        line = line.trim();
        builder.append(line);
    }

}