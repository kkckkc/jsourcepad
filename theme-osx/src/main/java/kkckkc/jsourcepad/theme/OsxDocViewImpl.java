package kkckkc.jsourcepad.theme;

import javax.swing.BorderFactory;

import kkckkc.jsourcepad.ui.DocViewImpl;
import kkckkc.syntaxpane.ScrollableSourcePane;

public class OsxDocViewImpl extends DocViewImpl {

	@Override
	protected ScrollableSourcePane createScrollableSource() {
		ScrollableSourcePane ssp = super.createScrollableSource();
		ssp.setBorder(BorderFactory.createEmptyBorder());
		ssp.getScrollPane().setBorder(BorderFactory.createEmptyBorder());
		return ssp;
	}
	
}
