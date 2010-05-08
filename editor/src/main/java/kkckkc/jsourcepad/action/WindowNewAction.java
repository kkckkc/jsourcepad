package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.WindowManager;



public class WindowNewAction extends AbstractAction {

	private Application app;

	public WindowNewAction(Application app) {
		super("New");
		this.app = app;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		WindowManager wm = app.getWindowManager();
		Window w = wm.newWindow(new File("."));
		w.getDocList().create();
	}

}
