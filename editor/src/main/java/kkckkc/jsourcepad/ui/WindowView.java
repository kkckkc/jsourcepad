package kkckkc.jsourcepad.ui;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import kkckkc.jsourcepad.View;

public interface WindowView extends View {

	public JMenuBar getMenubar();
	public JFrame getJFrame();

}
