package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

public class NavigationPreviousFileTabAction extends BaseAction {
    private final Window window;

	public NavigationPreviousFileTabAction(Window w) {
		this.window = w;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		int idx = window.getDocList().getActive();
		
		idx--;
		if (idx >= 0) {
			window.getDocList().setActive(idx);
		}
    }

}