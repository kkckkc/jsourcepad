package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.command.CompletionCommand;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class EditCompletionNextAction extends BaseAction {
	public EditCompletionNextAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void actionPerformed(ActionEvent e) {
        CompletionCommand command = new CompletionCommand();
        command.setDirection(CompletionCommand.Direction.NEXT);
        commandExecutor.execute(command);
    }

}