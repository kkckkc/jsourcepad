package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;

import kkckkc.jsourcepad.model.Window;

public class NavigationPreviousFileTabAction extends AbstractEditorAction {

	public NavigationPreviousFileTabAction(Window w) {
		super(w);
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