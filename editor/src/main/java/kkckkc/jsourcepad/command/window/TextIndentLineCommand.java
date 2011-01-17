package kkckkc.jsourcepad.command.window;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.util.command.AbstractWindowCommand;
import kkckkc.jsourcepad.util.command.WindowCommand;
import kkckkc.syntaxpane.model.Interval;

public class TextIndentLineCommand extends AbstractWindowCommand {
    @Override
    public void execute() {
        Buffer activeBuffer = window.getDocList().getActiveDoc().getActiveBuffer();
		Interval selectionOrCurrentLine = activeBuffer.getSelectionOrCurrentLine();
		activeBuffer.indent(selectionOrCurrentLine);
    }
}
