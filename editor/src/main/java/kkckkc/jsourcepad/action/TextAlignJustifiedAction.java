package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.StyleSettings;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.TextUtils;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.TextInterval;

import java.awt.event.ActionEvent;
import java.util.StringTokenizer;

public class TextAlignJustifiedAction extends BaseAction {
    private final Window window;

	public TextAlignJustifiedAction(Window window) {
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
            builder.append(TextUtils.justifyLine(line, ss.getWrapColumn()));
        }

		b.replaceText(i, builder.toString(), null);
    }

    private void justify(String line, StringBuilder b, int wrapColumn) {
        line = line.trim();
        line = line.replace(" +", " ");

        if (line.length() > wrapColumn) {
            b.append(line);
            return;
        }

        int spaceCount = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ') spaceCount++;
        }

        int charLength = line.length() - spaceCount;
        int newSpaceCount = wrapColumn - charLength;

        int spacing = (int) Math.ceil((double) newSpaceCount / spaceCount);

        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ') {
                for (int j = 0; j < spacing; j++) {
                    b.append(" ");
                }
            } else {
                b.append(line.charAt(i));
            }
        }
    }

}