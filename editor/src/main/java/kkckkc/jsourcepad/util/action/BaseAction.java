package kkckkc.jsourcepad.util.action;

import javax.swing.AbstractAction;

public abstract class BaseAction extends AbstractAction {
	private boolean active;

	public void activate(Object o) {
		this.active = true;
		setEnabled(shouldBeEnabled(o));
	}
	
	public void deactivate() {
		this.active = false;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public boolean shouldBeEnabled(Object source) {
		return true;
	}
}
