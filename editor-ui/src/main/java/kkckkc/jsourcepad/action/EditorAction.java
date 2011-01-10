package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.command.window.TextComponentCommand;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class EditorAction extends BaseAction {
    private String action;

	public EditorAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

    public void setAction(String action) {
        this.action = action;
    }

    public void setRequireSelection(boolean requireSelection) {
        if (requireSelection) {
            setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC, ActionStateRules.TEXT_SELECTED);
        }
    }

	@Override
    public void performAction(ActionEvent e) {
        TextComponentCommand command = new TextComponentCommand();
        command.setAction(this.action);

        commandExecutor.execute(command);
    }

}