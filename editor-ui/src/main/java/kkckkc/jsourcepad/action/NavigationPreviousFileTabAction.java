package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class NavigationPreviousFileTabAction extends BaseAction {
    private final Window window;

	public NavigationPreviousFileTabAction(Window w) {
		this.window = w;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}
	
	@Override
    public void performAction(ActionEvent e) {
		int idx = window.getDocList().getActive();
		
		idx--;
		if (idx >= 0) {
			window.getDocList().setActive(idx);
		}
    }

}