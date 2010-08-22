package kkckkc.jsourcepad.theme;

import java.awt.Color;

import javax.swing.plaf.TreeUI;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.Window.FocusListener;
import kkckkc.jsourcepad.ui.ProjectViewImpl;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;

public class OsxProjectViewImpl extends ProjectViewImpl {

	@Override
	public void init() {
		super.init();

		setBackground(new Color(213, 221, 229));
		
        try {
			setUI((TreeUI) Class.forName("kkckkc.jsourcepad.theme.TigerTreeUI").newInstance());
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		window.topic(Window.FocusListener.class).subscribe(DispatchStrategy.ASYNC_EVENT, new FocusListener() {
			public void focusGained(Window window) {
				setBackground(new Color(213, 221, 229));
			}

			public void focusLost(Window window) {
				setBackground(new Color(232, 232, 232));
			}
		});
		
		setFont(getFont().deriveFont(11f));
	}
	
}
