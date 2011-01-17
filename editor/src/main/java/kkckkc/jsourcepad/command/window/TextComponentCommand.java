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

    private ActionEvent actionEvent;

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

        if (actionEvent == null)
            actionEvent = new ActionEvent(buffer.getTextComponent(), (int) System.currentTimeMillis(), null);
        a.actionPerformed(actionEvent);
    }

    public void setActionEvent(ActionEvent e) {
        this.actionEvent = e;
    }
}
