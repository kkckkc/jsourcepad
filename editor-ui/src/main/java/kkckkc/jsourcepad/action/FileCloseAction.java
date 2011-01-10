package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.WindowManager;
import kkckkc.jsourcepad.util.action.BaseAction;

import javax.swing.*;
import java.awt.event.ActionEvent;



public class FileCloseAction extends BaseAction {

	private Application app;

	public FileCloseAction(Application app) {
		this.app = app;
	}
	
	@Override
	public void performAction(ActionEvent e) {
		WindowManager wm = app.getWindowManager();
		Window window = wm.getWindow((JComponent) e.getSource());
		wm.closeWindow(window);
	}

}
