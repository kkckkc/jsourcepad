package kkckkc.jsourcepad.ui.dialog.progress;

import kkckkc.jsourcepad.Dialog;
import kkckkc.jsourcepad.util.io.ScriptExecutor.Execution;
import kkckkc.jsourcepad.util.ui.ProgressDialog;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ProgressDialogImpl implements Dialog<ProgressDialogView>, ProgressDialog {

	private ProgressDialogView view;

	@Autowired
	public void setView(ProgressDialogView view) {
	    this.view = view;
	}

	@Override
    public void show(String title, final Execution execution, Container parent) {
		view.setTitle(title);
		
	    view.getJDialog().addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				execution.cancel();
			}
		});

	    view.getJDialog().setVisible(true);
	}
	
	@Override
    public void close() {
		view.close();

		// Clear old listeners
		for (WindowListener l : view.getJDialog().getWindowListeners()) {
			view.getJDialog().removeWindowListener(l);
		}
	}
}
