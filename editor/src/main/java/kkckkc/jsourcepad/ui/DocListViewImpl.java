package kkckkc.jsourcepad.ui;

import java.awt.Dimension;

import javax.swing.JTabbedPane;

public class DocListViewImpl implements DocListView {

	JTabbedPane tabbedPane;

    public DocListViewImpl() {
		tabbedPane = createTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(200, 0));
    }

	protected JTabbedPane createTabbedPane() {
	    return new JTabbedPane();
    }

	public JTabbedPane getTabbedPane() {
	    return tabbedPane;
    }
	
}
