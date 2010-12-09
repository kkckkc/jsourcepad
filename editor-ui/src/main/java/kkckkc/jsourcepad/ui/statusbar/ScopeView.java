package kkckkc.jsourcepad.ui.statusbar;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.InsertionPoint;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;

import javax.swing.*;

public class ScopeView extends JLabel implements Buffer.InsertionPointListener {

	public ScopeView(Window window) {
		setText("--");
		window.topic(Buffer.InsertionPointListener.class).subscribe(DispatchStrategy.ASYNC_EVENT, this);
	}
	
	@Override
    public void update(InsertionPoint ip) {
		if (ip == null || ip.getScope() == null) return;
		setText(ip.getScope().getPath());
    }

}
