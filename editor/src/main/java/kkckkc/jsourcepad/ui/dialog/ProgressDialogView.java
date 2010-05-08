package kkckkc.jsourcepad.ui.dialog;

import javax.swing.JDialog;

import kkckkc.jsourcepad.View;

public interface ProgressDialogView extends View {

	JDialog getJDialog();
	public void close();
	void setTitle(String title);
	
}
