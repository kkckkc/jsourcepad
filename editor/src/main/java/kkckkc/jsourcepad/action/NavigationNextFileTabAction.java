package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;

import kkckkc.jsourcepad.model.Window;

public class NavigationNextFileTabAction extends AbstractEditorAction {

	public NavigationNextFileTabAction(Window w) {
		super(w);
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