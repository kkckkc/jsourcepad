package kkckkc.jsourcepad.theme;

import javax.swing.border.EmptyBorder;

import kkckkc.jsourcepad.ui.DocViewImpl;
import kkckkc.syntaxpane.ScrollableSourcePane;

public class GtkDocViewImpl extends DocViewImpl {

	@Override
    protected ScrollableSourcePane createScrollableSource() {
	    ScrollableSourcePane ssp = super.createScrollableSource();
		ssp.getScrollPane().setBorder(new EmptyBorder(2, 2, 2, 2));
	    return ssp;
    }

}
