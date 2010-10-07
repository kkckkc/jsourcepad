package kkckkc.jsourcepad.ui.statusbar;

import javax.swing.JLabel;
import kkckkc.jsourcepad.model.Buffer;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.InsertionPoint;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;

public class CurrentPosition extends JLabel implements Buffer.InsertionPointListener {
	public CurrentPosition(Window window) {
		setText("--:--");	
		
		window.topic(Buffer.InsertionPointListener.class).subscribe(DispatchStrategy.ASYNC_EVENT, this);
	}

	@Override
    public void update(InsertionPoint ip) {
	    setText((ip.getLineNumber() + 1) + ":" + (ip.getLineIndex() + 1) + " (" + ip.getPosition() + ")");
    }
}
