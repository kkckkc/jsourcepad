package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.command.window.TextIndentLineCommand;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class TextIndentLineAction extends BaseAction {
    private final Window window;

	public TextIndentLineAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}
	
	@Override
    public void performAction(ActionEvent e) {
        window.getCommandExecutor().execute(new TextIndentLineCommand());
    }

}
