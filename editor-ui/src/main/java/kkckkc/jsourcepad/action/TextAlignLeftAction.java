package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.command.window.TextAlignLeftCommand;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.settings.StyleSettings;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.TextInterval;

import java.awt.event.ActionEvent;
import java.util.StringTokenizer;

public class TextAlignLeftAction extends BaseAction {
    private final Window window;

	public TextAlignLeftAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void performAction(ActionEvent e) {
        window.getCommandExecutor().execute(new TextAlignLeftCommand());
    }

}