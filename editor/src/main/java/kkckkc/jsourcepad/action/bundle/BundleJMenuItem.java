package kkckkc.jsourcepad.action.bundle;

import javax.swing.JMenuItem;

public class BundleJMenuItem extends JMenuItem {

	public BundleJMenuItem(BundleAction da) {
		super(da);
		setAccelerator(da.getRef().getActivator().getKeyStroke());
		if (da.getRef().getActivator().getTabTrigger() != null) {
			setText(getText() + "    " + da.getRef().getActivator().getTabTrigger() + " ->");
		}
	}
	
}
