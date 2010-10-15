package kkckkc.jsourcepad.theme.gtk;

import kkckkc.jsourcepad.ui.DocViewImpl;
import kkckkc.syntaxpane.ScrollableSourcePane;

import javax.swing.border.EmptyBorder;

public class GtkDocViewImpl extends DocViewImpl {

	@Override
    protected ScrollableSourcePane createScrollableSource() {
	    ScrollableSourcePane ssp = super.createScrollableSource();
		ssp.getScrollPane().setBorder(new EmptyBorder(2, 2, 2, 2));
	    return ssp;
    }

}
