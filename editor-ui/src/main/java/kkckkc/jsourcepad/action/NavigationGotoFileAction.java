package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.dialog.navigation.QuickNavigationDialog;
import kkckkc.jsourcepad.util.action.BaseAction;

public class NavigationGotoFileAction extends BaseAction {

	private Window window;

	public NavigationGotoFileAction(Window w) {
		this.window = w;
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		QuickNavigationDialog dialog = window.getPresenter(QuickNavigationDialog.class);
		dialog.show();
    }

}