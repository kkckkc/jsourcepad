package kkckkc.jsourcepad.model.bundle;

import kkckkc.syntaxpane.model.Scope;
import kkckkc.syntaxpane.style.ScopeSelector;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface BundleManager {
    // Bundles
	public List<Bundle> getBundles();
    public Bundle getBundle(String name);
    public Bundle getBundle(File dir);

    // Preferences
	public Map<String, Map<ScopeSelector, Object>> getPreferences();
	public Object getPreference(String key, Scope scope);

    // Misc
	public void reload();
	public void reload(Bundle bundle);
    public void addBundle(File dir);
    public void remove(Bundle bundle);

    // Items
	public Collection<BundleItemSupplier> getItemsForShortcut(KeyEvent ks, Scope scope);
	public Collection<BundleItemSupplier> getItemsForTabTrigger(String trigger, Scope scope);

    // Properties
    public File getBundleDir();

    public interface Listener {
        public void bundleAdded(Bundle bundle);
        public void bundleRemoved(Bundle bundle);
        public void bundleUpdated(Bundle bundle);
    }

    public void addListener(Listener listener);
    public void removeListener(Listener listener);
}
