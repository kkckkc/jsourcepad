package kkckkc.jsourcepad.ui.dialog;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import org.springframework.beans.factory.annotation.Autowired;

import kkckkc.jsourcepad.Dialog;
import kkckkc.jsourcepad.util.io.ScriptExecutor.Execution;

public class ProgressDialog implements Dialog<ProgressDialogView> {

	private ProgressDialogView view;

	@Autowired
	public void setView(ProgressDialogView view) {
	    this.view = view;
	}

	public void show(String title, final Execution execution, Frame parent) {
		view.setTitle(title);
		
	    view.getJDialog().addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				execution.cancel();
				close();
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
