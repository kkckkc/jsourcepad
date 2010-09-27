package kkckkc.jsourcepad.model.bundle;

import kkckkc.syntaxpane.style.ScopeSelector;

import java.util.List;
import java.util.Map;

public class Bundle {
	private String name;
	private List<Object> menu;
	private Map<String, Map<ScopeSelector, Object>> preferences;
	private List<BundleItemSupplier> items;
	
	public Bundle(String name, List<Object> menu, Map<String, Map<ScopeSelector, Object>> preferences, List<BundleItemSupplier> items) {
	    this.name = name;
	    this.menu = menu;
	    this.preferences = preferences;
	    this.items = items;
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

	public List<BundleItemSupplier> getItems() {
	    return items;
    }
}
