package kkckkc.jsourcepad.theme.gtk;

import kkckkc.jsourcepad.ui.WindowViewImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class GtkWindowViewImpl extends WindowViewImpl {

	@Override
    protected JSplitPane createSplitPane() {
	    JSplitPane sp = super.createSplitPane();
		sp.setDividerSize(3);
	    return sp;
    }

	@Override
    protected JScrollPane createTreeScrollPane(JComponent tree) {
	    JScrollPane jsp = super.createTreeScrollPane(tree);
		jsp.setBorder(new EmptyBorder(2, 2, 0, 0));
	    return jsp;
    }
}
