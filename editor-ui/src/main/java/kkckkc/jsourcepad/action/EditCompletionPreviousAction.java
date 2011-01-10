package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.command.window.CompletionCommand;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class EditCompletionPreviousAction extends BaseAction {
	public EditCompletionPreviousAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void performAction(ActionEvent e) {
        CompletionCommand command = new CompletionCommand();
        command.setDirection(CompletionCommand.Direction.PREVIOUS);
        commandExecutor.execute(command);
    }

}