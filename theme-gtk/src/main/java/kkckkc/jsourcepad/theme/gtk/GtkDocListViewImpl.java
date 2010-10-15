package kkckkc.jsourcepad.theme.gtk;

import kkckkc.jsourcepad.ui.DocListViewImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class GtkDocListViewImpl extends DocListViewImpl {

	@Override
    protected JTabbedPane createTabbedPane() {
	    JTabbedPane jtp = super.createTabbedPane();
		jtp.setBorder(new EmptyBorder(4, 1, 0, 2));
		return jtp;
    }

	
	
}
