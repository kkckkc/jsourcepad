package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.dialog.navigation.SymbolNavigationDialog;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class NavigationGotoSymbolAction extends BaseAction {

	private Window window;

	public NavigationGotoSymbolAction(Window w) {
		this.window = w;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}


    
	@Override
    public void actionPerformed(ActionEvent e) {
		SymbolNavigationDialog dialog = window.getPresenter(SymbolNavigationDialog.class);
		dialog.show();
    }

}