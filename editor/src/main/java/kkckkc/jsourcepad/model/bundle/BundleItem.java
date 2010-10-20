package kkckkc.jsourcepad.model.bundle;

import kkckkc.jsourcepad.model.Window;


public interface BundleItem<C> {
	public void execute(Window window, C context) throws Exception;
    public BundleStructure.Type getType();
}
