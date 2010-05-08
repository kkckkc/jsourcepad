package kkckkc.jsourcepad.model.bundle;

import java.io.File;

import javax.swing.Action;

import com.google.common.base.Supplier;

public class BundleItemSupplier implements Supplier<BundleItem> {

	private File file;
	private String name;
	private Activator activator;
	private Action action;

	public BundleItemSupplier(File file, String name, Activator activator) {
	    this.file = file;
	    this.name = name;
	    this.activator = activator;
    }

	public Activator getActivator() {
	    return activator;
    }
	
	public String getName() {
	    return name;
    }
	
	public String toString() {
		return this.name;
	}
	
    public BundleItem get() {
	    return BundleItemFactory.getItem(this, file);
    }

	public File getFile() {
	    return file;
    }

	public void setAction(Action action) {
	    this.action = action;
    }
	
	public Action getAction() {
	    return action;
    }

}
