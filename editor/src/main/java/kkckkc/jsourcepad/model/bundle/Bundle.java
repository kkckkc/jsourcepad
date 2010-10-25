package kkckkc.jsourcepad.model.bundle;

import kkckkc.syntaxpane.style.ScopeSelector;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Bundle {
	private String name;
	private List<Object> menu;
	private Map<String, Map<ScopeSelector, Object>> preferences;
    private Map<String, BundleItemSupplier> itemsByUuid;
    private File dir;

    public Bundle(String name,
                  List<Object> menu, 
                  Map<String, Map<ScopeSelector, Object>> preferences,
                  Map<String, BundleItemSupplier> itemsByUuid,
                  File dir) {
	    this.name = name;
	    this.menu = menu;
	    this.preferences = preferences;
        this.itemsByUuid = itemsByUuid;
        this.dir = dir;
    }

    public File getDir() {
        return dir;
    }

    public Map<String, Map<ScopeSelector, Object>> getPreferences() {
	    return preferences;
    }
	
	public String getName() {
	    return this.name;
    }

	public List<Object> getMenu() {
	    return menu;
    }

	public Collection<BundleItemSupplier> getItems() {
	    return itemsByUuid.values();
    }

    public Map<String, BundleItemSupplier> getItemsByUuid() {
        return itemsByUuid;
    }
}
