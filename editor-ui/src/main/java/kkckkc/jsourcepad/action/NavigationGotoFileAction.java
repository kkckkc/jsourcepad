package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.dialog.navigation.QuickNavigationDialog;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class NavigationGotoFileAction extends BaseAction {

	private Window window;

	public NavigationGotoFileAction(Window w) {
		this.window = w;
        setActionStateRules(ActionStateRules.HAS_PROJECT);
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		QuickNavigationDialog dialog = window.getPresenter(QuickNavigationDialog.class);
		dialog.show();
    }

}