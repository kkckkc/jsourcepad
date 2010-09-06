package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.WindowManager;
import kkckkc.jsourcepad.util.action.BaseAction;



public class WindowCloseAction extends BaseAction {

	private Application app;

	public WindowCloseAction(Application app) {
		this.app = app;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		WindowManager wm = app.getWindowManager();
		Window window = wm.getWindow((JComponent) e.getSource());
		wm.closeWindow(window);
	}

}
