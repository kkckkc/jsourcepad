package kkckkc.jsourcepad.ui;

import kkckkc.jsourcepad.View;

import javax.swing.*;

public interface WindowView extends View {
	public JMenuBar getMenubar();
	public void setJFrame(JFrame frame);
    public void setShowProjectDrawer(boolean showProjectDrawer);
}
