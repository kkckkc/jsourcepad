package kkckkc.jsourcepad.ui;

import kkckkc.jsourcepad.View;

import javax.swing.*;

public interface WindowView extends View {

	public JMenuBar getMenubar();
	public JFrame getJFrame();

    public void setShowProjectDrawer(boolean showProjectDrawer);
}
