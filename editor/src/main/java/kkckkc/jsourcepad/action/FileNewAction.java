package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

public class FileNewAction extends BaseAction {

	private Application app;

	public FileNewAction(Application app) {
		this.app = app;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final Window window = app.getWindowManager().getWindow((JComponent) e.getSource());
		window.getDocList().create();
	}
	
	
}
