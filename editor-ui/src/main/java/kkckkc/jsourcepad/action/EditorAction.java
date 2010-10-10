package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.command.TextComponentCommand;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.util.action.BaseAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class EditorAction extends BaseAction {
    private String action;

	public EditorAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

    public void setAction(String action) {
        this.action = action;
    }

    public void setRequireSelection(boolean b) {
        if (b) {
            setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC, ActionStateRules.TEXT_SELECTED);
        }
    }

	@Override
    public void actionPerformed(ActionEvent e) {
        TextComponentCommand command = new TextComponentCommand();
        command.setAction(this.action);

        commandExecutor.execute(command);
    }

}