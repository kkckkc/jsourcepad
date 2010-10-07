package kkckkc.jsourcepad.util.ui;

import kkckkc.jsourcepad.Dialog;
import kkckkc.jsourcepad.util.io.ScriptExecutor.Execution;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ProgressDialog implements Dialog<ProgressDialogView> {

	private ProgressDialogView view;

	@Autowired
	public void setView(ProgressDialogView view) {
	    this.view = view;
	}

	public void show(String title, final Execution execution, Container parent) {
		view.setTitle(title);
		
	    view.getJDialog().addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				execution.cancel();
			}
		});

	    view.getJDialog().setVisible(true);
	}
	
	public void close() {
		view.close();

		// Clear old listeners
		for (WindowListener l : view.getJDialog().getWindowListeners()) {
			view.getJDialog().removeWindowListener(l);
		}
	}
}
