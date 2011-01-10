package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class FileSaveAction extends BaseAction {

	private FileSaveAsAction fileSaveAsAction;

	public FileSaveAction(FileSaveAsAction fileSaveAsAction) {
		this.fileSaveAsAction = fileSaveAsAction;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC, ActionStateRules.DOC_IS_MODIFIED);
	}

	@Override
	public void performAction(ActionEvent e) {
        Doc activeDoc = actionContext.get(ActionContextKeys.ACTIVE_DOC);
		if (! activeDoc.isBackedByFile()) {
            fileSaveAsAction.setActionContext(actionContext);
			fileSaveAsAction.performAction(e);
		} else {
			activeDoc.save();
		}
	}

}
