package kkckkc.jsourcepad.model.bundle;

import java.util.List;
import java.util.Map;

import kkckkc.jsourcepad.util.action.ActionGroup;
import kkckkc.syntaxpane.style.ScopeSelector;

public class Bundle {
	private String name;
	private ActionGroup menu;
	private Map<String, Map<ScopeSelector, Object>> preferences;
	private List<BundleItemSupplier> items;
	
	public Bundle(String name, ActionGroup menu, Map<String, Map<ScopeSelector, Object>> preferences, List<BundleItemSupplier> items) {
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

	public ActionGroup getMenu() {
	    return menu;
    }

	public List<BundleItemSupplier> getItems() {
	    return items;
    }
}
