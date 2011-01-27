package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.dialog.find.FindDialog;
import kkckkc.jsourcepad.ui.dialog.find.FindInProjectDialog;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class EditFindFindInProjectAction extends BaseAction {
    private final Window window;

	public EditFindFindInProjectAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_PROJECT);
	}
	
	@Override
    public void performAction(ActionEvent e) {
		FindInProjectDialog dialog = window.getPresenter(FindInProjectDialog.class);
		dialog.show();
    }

}
