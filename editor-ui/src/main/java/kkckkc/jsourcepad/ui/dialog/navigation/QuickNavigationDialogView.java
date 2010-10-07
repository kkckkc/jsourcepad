package kkckkc.jsourcepad.ui.dialog.navigation;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;

import kkckkc.jsourcepad.View;

public interface QuickNavigationDialogView extends View {
	public JTextField getTextField();
	public JList getResult();
	public JDialog getJDialog();
	public JLabel getPath();
}