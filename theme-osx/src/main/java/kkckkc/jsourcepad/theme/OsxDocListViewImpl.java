package kkckkc.jsourcepad.theme;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

import kkckkc.jsourcepad.ui.DocListViewImpl;

public class OsxDocListViewImpl extends DocListViewImpl {

	@Override
	protected JTabbedPane createTabbedPane() {
		JTabbedPane jtp = super.createTabbedPane();
		jtp.setUI(new DocumentTabbedPaneUI());
		jtp.setBorder(BorderFactory.createEmptyBorder());
		return jtp;
	}
	
}
