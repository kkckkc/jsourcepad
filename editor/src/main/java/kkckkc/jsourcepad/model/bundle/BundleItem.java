package kkckkc.jsourcepad.model.bundle;

import kkckkc.jsourcepad.model.Window;


public interface BundleItem {
	public void execute(Window window) throws Exception;
	public BundleItemSupplier getBundleItemRef();
}
