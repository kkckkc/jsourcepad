package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.WindowManager;



public class WindowCloseAction extends AbstractAction {

	private Application app;

	public WindowCloseAction(Application app) {
		super("Close");
		this.app = app;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		WindowManager wm = app.getWindowManager();
		Window window = wm.getWindow((JComponent) e.getSource());
		wm.closeWindow(window);
	}

}
