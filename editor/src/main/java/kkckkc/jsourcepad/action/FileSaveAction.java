package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.ActionContext;
import kkckkc.jsourcepad.util.action.BaseAction;

public class FileSaveAction extends BaseAction {

	private FileSaveAsAction fileSaveAsAction;
    private final Window window;

	public FileSaveAction(Window window, FileSaveAsAction fileSaveAsAction) {
		this.window = window;
		this.fileSaveAsAction = fileSaveAsAction;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC, ActionStateRules.DOC_IS_MODIFIED);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (! window.getDocList().getActiveDoc().isBackedByFile()) {
			fileSaveAsAction.actionPerformed(e);
		} else {
			window.getDocList().getActiveDoc().save();
		}
	}

}
