package kkckkc.jsourcepad.model.bundle;

import com.google.common.base.Supplier;

import javax.swing.*;
import java.io.File;

public class BundleItemSupplier implements Supplier<BundleItem> {

	private File file;
	private String name;
	private Activator activator;
	private Action action;
    private BundleStructure.Type type;
    private String uuid;

    public BundleItemSupplier(File file, String uuid, String name, Activator activator, BundleStructure.Type type) {
	    this.uuid = uuid;
        this.file = file;
	    this.name = name;
	    this.activator = activator;
        this.type = type;
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

    public BundleStructure.Type getType() {
        return type;
    }

    public String getUUID() {
        return uuid;
    }
}
