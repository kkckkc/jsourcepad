package kkckkc.jsourcepad.util.ui;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

public class ComponentUtils {
	public static Component getToplevelAncestor(Container c) {
		for (Container p = c; p != null; p = p.getParent()) {
			if (p instanceof Window || p instanceof Applet) {
				return p;
			} else if (p instanceof JPopupMenu) {
				return getToplevelAncestor((JComponent) ((JPopupMenu) p).getInvoker());
			}
		}
		return null;
	}
}
