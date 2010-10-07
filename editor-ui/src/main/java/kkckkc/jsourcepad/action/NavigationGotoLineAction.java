package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.dialog.gotoline.GotoLineDialog;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class NavigationGotoLineAction extends BaseAction {

	private Window window;

	public NavigationGotoLineAction(Window w) {
		this.window = w;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void actionPerformed(ActionEvent e) {
        GotoLineDialog gotoLineDialog = window.getPresenter(GotoLineDialog.class);
        gotoLineDialog.show();
    }

}