package kkckkc.jsourcepad.util.ui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

public class PopupUtils {
    public static void bind(final JPopupMenu jp, final Component component, final boolean allButtons, final PopupListener listener) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e) {
				if (allButtons || e.isPopupTrigger()) {
                    if (listener != null) {
                        listener.show(e);
                    }
                    jp.show(component, (int) e.getPoint().getX(), (int) e.getPoint().getY());
				}
			}
		});
    }

    public static void bind(final JPopupMenu jp, final Component component, final boolean allButtons) {
        bind(jp, component, allButtons, null);
    }

    public interface PopupListener {
        public void show(MouseEvent e);
    }
}
