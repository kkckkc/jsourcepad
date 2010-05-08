package kkckkc.jsourcepad.theme;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import kkckkc.jsourcepad.ui.ProjectViewImpl;
import kkckkc.jsourcepad.ui.WindowViewImpl;

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
