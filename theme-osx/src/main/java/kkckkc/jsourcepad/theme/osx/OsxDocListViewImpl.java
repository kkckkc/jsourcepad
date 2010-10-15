package kkckkc.jsourcepad.theme.osx;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.Window.FocusListener;
import kkckkc.jsourcepad.ui.DocListViewImpl;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.swing.*;

public class OsxDocListViewImpl extends DocListViewImpl {

	private Window window;
	private JTabbedPane jtp;

	@PostConstruct
	public void init() {
		jtp = super.createTabbedPane();
		final DocumentTabbedPaneUI ui = new DocumentTabbedPaneUI();
		jtp.setUI(ui);
		jtp.setBorder(BorderFactory.createEmptyBorder());

		window.topic(Window.FocusListener.class).subscribe(DispatchStrategy.ASYNC_EVENT, new FocusListener() {
			public void focusGained(Window window) {
				ui.setFocused(true);
				jtp.repaint();
			}

			public void focusLost(Window window) {
				ui.setFocused(false);
				jtp.repaint();
			}
		});
	}
	
	@Autowired
	public void setWindow(Window window) {
	    this.window = window;
    }
	
	@Override
	protected JTabbedPane createTabbedPane() {
		return jtp;
	}
}
