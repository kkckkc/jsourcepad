package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.DocPresenter;
import kkckkc.jsourcepad.ui.dialog.find.FindDialog;
import kkckkc.jsourcepad.ui.dialog.find.FindDialogView;
import kkckkc.jsourcepad.util.action.BaseAction;

public class EditFindAction extends BaseAction {
    private final Window window;

	public EditFindAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		FindDialog dialog = window.getPresenter(FindDialog.class);
		dialog.show();
    }

}
