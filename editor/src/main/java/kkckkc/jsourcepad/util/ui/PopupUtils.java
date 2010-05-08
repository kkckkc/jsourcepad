package kkckkc.jsourcepad.util.ui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class PopupUtils {
	public static final String REQUESTED_LOCATION = "requestedLocation";
	
	public static void bind(final JPopupMenu jp, final Component component, final boolean allButtons) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e) {
				if (allButtons || e.isPopupTrigger()) {
					show(jp, e.getPoint(), e.getComponent());
				}
			}
		});
	}
	
	public static void show(JPopupMenu jpm, Point point, Component component) {
		jpm.putClientProperty(REQUESTED_LOCATION, point);
		jpm.show(component, (int) point.getX(), (int) point.getY());
	}
	
	public static Component getInvoker(Object source) {
		JPopupMenu jpm = getJPopupMenu(source);
		if (jpm == null) return null;

		return jpm.getInvoker();
	}
	
	public static Component getInvoker(ActionEvent e) {
		return getInvoker(e.getSource());
	}

	public static Point getRequestedLocation(Object source) {
		JPopupMenu jpm = getJPopupMenu(source);
		if (jpm == null) return null;
		
		return (Point) jpm.getClientProperty(REQUESTED_LOCATION);
	}

	public static Point getRequestedLocation(ActionEvent e) {
		return getRequestedLocation(e.getSource());
	}

	private static JPopupMenu getJPopupMenu(Object source) {
		if (! (source instanceof JMenuItem)) return null;
		JMenuItem jmi = (JMenuItem) source;
		
		if (! (jmi.getParent() instanceof JPopupMenu)) return null;
		JPopupMenu jpm = (JPopupMenu) jmi.getParent();

		return jpm;
	}
}
