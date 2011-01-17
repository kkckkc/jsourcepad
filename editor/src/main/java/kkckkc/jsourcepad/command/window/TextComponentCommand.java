package kkckkc.jsourcepad.command.window;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.jsourcepad.util.command.AbstractWindowCommand;
import kkckkc.jsourcepad.util.command.CommandProperty;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class TextComponentCommand extends AbstractWindowCommand {
    @CommandProperty private String action;

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public void execute() {
        Doc doc = window.getDocList().getActiveDoc();
        Buffer buffer = doc.getActiveBuffer();

        Action a = buffer.getActionMap().get(this.action);

        if (a instanceof BaseAction) {
            BaseAction baseAction = (BaseAction) a;
            if (baseAction.getDelegate() != null) a = baseAction.getDelegate();
        }

        ActionEvent event = new ActionEvent(buffer.getTextComponent(), (int) System.currentTimeMillis(), null);
        a.actionPerformed(event);
    }
}
