package kkckkc.jsourcepad.ui.dialog.progress;

import kkckkc.jsourcepad.View;

import javax.swing.*;

public interface ProgressDialogView extends View {

	JDialog getJDialog();
	public void close();
	void setTitle(String title);
	
}
