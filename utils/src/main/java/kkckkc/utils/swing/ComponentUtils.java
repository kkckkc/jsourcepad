package kkckkc.utils.swing;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;

public class ComponentUtils {
	public static Component getToplevelAncestor(Component component) {
		for (Component p = component; p != null; p = p.getParent()) {
			if (p instanceof Window || p instanceof Applet) {
				return p;
			} else if (p instanceof JPopupMenu) {
				return getToplevelAncestor((JComponent) ((JPopupMenu) p).getInvoker());
			}
		}
		return null;
	}
}
