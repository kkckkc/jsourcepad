package kkckkc.jsourcepad.util.ui;

import javax.swing.JDialog;

import kkckkc.jsourcepad.View;

public interface ProgressDialogView extends View {

	JDialog getJDialog();
	public void close();
	void setTitle(String title);
	
}
