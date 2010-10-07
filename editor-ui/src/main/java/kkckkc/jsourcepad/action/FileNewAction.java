package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class FileNewAction extends BaseAction {

	public FileNewAction() {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final Window window = Application.get().getWindowManager().getWindow((JComponent) e.getSource());
		window.getDocList().create();
	}
	
	
}
