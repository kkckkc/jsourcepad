package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

public class FileSaveAllAction extends BaseAction {

	private FileSaveAction fileSaveAction;
    private final Window window;

	public FileSaveAllAction(Window window, FileSaveAction fileSaveAction) {
		this.window = window;
		this.fileSaveAction = fileSaveAction;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < window.getDocList().getDocCount(); i++) {
            window.getDocList().setActive(i);
            fileSaveAction.actionPerformed(e);
        }
	}
}
