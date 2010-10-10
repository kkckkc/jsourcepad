package kkckkc.jsourcepad.command;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.command.Command;

import javax.swing.*;

public class TextComponentCommand implements Command {
    private String action;

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public void execute(Window window) {
        Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();

        Action a = buffer.getActionMap().get(this.action);
        a.actionPerformed(null);
    }
}
