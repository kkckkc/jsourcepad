package kkckkc.jsourcepad.theme;

import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import kkckkc.jsourcepad.ui.DocListViewImpl;

public class GtkDocListViewImpl extends DocListViewImpl {

	@Override
    protected JTabbedPane createTabbedPane() {
	    JTabbedPane jtp = super.createTabbedPane();
		jtp.setBorder(new EmptyBorder(4, 1, 0, 2));
		return jtp;
    }

	
	
}
