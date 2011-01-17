package kkckkc.jsourcepad.command.window;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.util.command.AbstractWindowCommand;
import kkckkc.syntaxpane.model.TextInterval;

import java.util.StringTokenizer;

public class TextAlignLeftCommand extends AbstractWindowCommand {
    @Override
    public void execute() {
        Buffer activeBuffer = window.getDocList().getActiveDoc().getActiveBuffer();

        TextInterval selectionOrCurrentLine = activeBuffer.getSelectionOrCurrentLine();
        String text = selectionOrCurrentLine.getText();

        StringBuilder builder = new StringBuilder();
        StringTokenizer tok = new StringTokenizer(text, "\n", true);
        while (tok.hasMoreTokens()) {
            String line = tok.nextToken();
            if (line.equals("\n")) builder.append(line);
            else builder.append(line.trim());
        }

        activeBuffer.replaceText(selectionOrCurrentLine, builder.toString(), null);
    }
}
