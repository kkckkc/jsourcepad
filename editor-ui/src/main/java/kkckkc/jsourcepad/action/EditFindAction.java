package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.dialog.find.FindDialog;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class EditFindAction extends BaseAction {
    private final Window window;

	public EditFindAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}
	
	@Override
    public void performAction(ActionEvent e) {
		FindDialog dialog = window.getPresenter(FindDialog.class);
		dialog.show();

        actionManager.updateActionState();
    }

}
