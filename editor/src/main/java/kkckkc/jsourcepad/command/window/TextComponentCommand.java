package kkckkc.jsourcepad.command.window;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.util.command.AbstractWindowCommand;

import javax.swing.*;

public class TextComponentCommand extends AbstractWindowCommand {
    private String action;

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public void execute() {
        Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();

        Action a = buffer.getActionMap().get(this.action);
        a.actionPerformed(null);
    }
}
