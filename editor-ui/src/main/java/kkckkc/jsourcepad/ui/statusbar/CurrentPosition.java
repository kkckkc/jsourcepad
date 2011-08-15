package kkckkc.jsourcepad.ui.statusbar;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.InsertionPoint;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;

import javax.swing.*;

public class CurrentPosition extends JLabel implements Buffer.InsertionPointListener {
	public CurrentPosition(Window window) {
		setText("--:--");	
		
		window.topic(Buffer.InsertionPointListener.class).subscribe(DispatchStrategy.EVENT_ASYNC, this);
	}

	@Override
    public void update(InsertionPoint ip) {
	    setText((ip.getLineNumber() + 1) + ":" + (ip.getLineIndex() + 1));
    }
}
