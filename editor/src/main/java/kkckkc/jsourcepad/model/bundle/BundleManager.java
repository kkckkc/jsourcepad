package kkckkc.jsourcepad.model.bundle;

import kkckkc.jsourcepad.util.action.ActionGroup;
import kkckkc.syntaxpane.model.Scope;
import kkckkc.syntaxpane.style.ScopeSelector;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface BundleManager { 
	public Map<String, List<Bundle>> getBundles();

    public Bundle getBundle(String name);

	public ActionGroup getBundleActionGroup();
	
	public Map<String, Map<ScopeSelector, Object>> getPreferences();
	public Object getPreference(String key, Scope scope);
	
	public void reload();

	public Collection<BundleItemSupplier> getItemsForShortcut(KeyEvent ks, Scope scope);

	public Collection<BundleItemSupplier> getItemsForTabTrigger(String trigger, Scope scope);

    public File getBundleDir();
}
