package kkckkc.jsourcepad.ui.dialog.navigation;

import kkckkc.jsourcepad.View;

import javax.swing.*;

public interface QuickNavigationDialogView extends View {
	public JTextField getTextField();
	public JList getResult();
	public JDialog getJDialog();
	public JLabel getPath();
}