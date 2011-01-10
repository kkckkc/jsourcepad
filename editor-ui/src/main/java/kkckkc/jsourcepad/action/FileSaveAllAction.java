package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class FileSaveAllAction extends BaseAction {

	private FileSaveAction fileSaveAction;
    private final Window window;

	public FileSaveAllAction(Window window, FileSaveAction fileSaveAction) {
		this.window = window;
		this.fileSaveAction = fileSaveAction;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
	public void performAction(ActionEvent e) {
        for (int i = 0; i < window.getDocList().getDocCount(); i++) {
            window.getDocList().setActive(i);
            fileSaveAction.performAction(e);
        }
	}
}
