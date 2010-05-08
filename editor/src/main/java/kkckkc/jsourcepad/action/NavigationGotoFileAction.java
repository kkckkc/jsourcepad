package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.dialog.navigation.QuickNavigationDialog;

public class NavigationGotoFileAction extends AbstractEditorAction {

	private Window window;

	public NavigationGotoFileAction(Window w) {
		super(w);
		this.window = w;
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		QuickNavigationDialog dialog = window.getPresenter(QuickNavigationDialog.class);
		dialog.show();
    }

}