package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.dialog.navigation.QuickNavigationDialog;

public class NavigationGotoSymbolAction extends AbstractEditorAction {

	private Window window;

	public NavigationGotoSymbolAction(Window w) {
		super(w);
		this.window = w;
	}
	
	//TODO: Implement this
	@Override
    public void actionPerformed(ActionEvent e) {
		QuickNavigationDialog dialog = window.getPresenter(QuickNavigationDialog.class);
		dialog.show();
    }

}