package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class NavigationNextFileTabAction extends BaseAction {
    private final Window window;

	public NavigationNextFileTabAction(Window w) {
		this.window = w;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		int idx = window.getDocList().getActive();
		
		idx++;
		if (idx < window.getDocList().getDocCount()) {
			window.getDocList().setActive(idx);
		}
    }

}