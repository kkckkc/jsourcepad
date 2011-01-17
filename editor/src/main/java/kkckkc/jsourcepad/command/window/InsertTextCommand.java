package kkckkc.jsourcepad.command.window;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.util.command.AbstractWindowCommand;
import kkckkc.jsourcepad.util.command.CommandProperty;

public class InsertTextCommand extends AbstractWindowCommand {

    private boolean noExecute;
    @CommandProperty private String text;

    public void setNoExecute(boolean noExecute) {
        this.noExecute = noExecute;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void execute() {
        if (noExecute) return;

        Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();
        buffer.insertText(buffer.getInsertionPoint().getPosition(), text, null);
    }
}
