package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.StyleSettings;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.TextInterval;

import java.awt.event.ActionEvent;
import java.util.StringTokenizer;

public class TextAlignCenterAction extends BaseAction {
    private final Window window;

	public TextAlignCenterAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void actionPerformed(ActionEvent e) {
        StyleSettings ss = Application.get().getSettingsManager().get(StyleSettings.class);

		Buffer b = window.getDocList().getActiveDoc().getActiveBuffer();

		TextInterval i = b.getSelectionOrCurrentLine();
        String text = i.getText();

        StringBuilder builder = new StringBuilder();
        StringTokenizer tok = new StringTokenizer(text, "\n", true);
        while (tok.hasMoreTokens()) {
            String line = tok.nextToken();
            if (line.equals("\n")) builder.append(line);
            center(line, builder, ss.getWrapColumn());
        }

		b.replaceText(i, builder.toString(), null);
    }

    private void center(String line, StringBuilder b, int wrapColumn) {
        line = line.trim();

        if (line.length() > wrapColumn) {
            b.append(line);
            return;
        }

        int mid = wrapColumn / 2;
        int indent = mid - (line.length() / 2);

        for (int i = 0; i < indent; i++) {
            b.append(" ");
        }
        b.append(line);
    }

}