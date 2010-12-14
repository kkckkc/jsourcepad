package kkckkc.jsourcepad.ui;

import javax.swing.*;
import java.awt.*;

public class DocListViewImpl implements DocListView {

	JTabbedPane tabbedPane;

    public DocListViewImpl() {
    }

	protected JTabbedPane createTabbedPane() {
	    tabbedPane = new JTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(200, 0));
		return tabbedPane;
    }

	public JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = createTabbedPane();
		}
		return tabbedPane;
    }
	
}
