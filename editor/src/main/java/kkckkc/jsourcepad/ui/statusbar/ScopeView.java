package kkckkc.jsourcepad.ui.statusbar;

import javax.swing.JLabel;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.InsertionPoint;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;

public class ScopeView extends JLabel implements Doc.InsertionPointListener {

	public ScopeView(Window window) {
		setText("--");
		window.topic(Doc.InsertionPointListener.class).subscribe(DispatchStrategy.ASYNC_EVENT, this);
	}
	
	@Override
    public void update(InsertionPoint ip) {
		if (ip == null || ip.getScope() == null) return;
		setText(ip.getScope().getPath());
    }

}
