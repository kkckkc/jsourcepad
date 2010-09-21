package kkckkc.jsourcepad.model.bundle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import kkckkc.jsourcepad.action.bundle.BundleAction;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.util.PerformanceLogger;
import kkckkc.jsourcepad.util.action.ActionGroup;
import kkckkc.syntaxpane.model.Scope;
import kkckkc.syntaxpane.style.ScopeSelector;
import kkckkc.syntaxpane.style.ScopeSelectorManager;
import kkckkc.syntaxpane.util.plist.GeneralPListReader;
import kkckkc.syntaxpane.util.plist.PListReader;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;

public class BundleManagerImpl implements BundleManager {

	private Map<String, List<Bundle>> bundles;
	private HashMap<String, Map<ScopeSelector, Object>> preferences;

	private String bundleDir;
	
	public BundleManagerImpl(String bundleDir) {
		this.bundleDir = bundleDir.replace("~", System.getProperty("user.home"));
	}
	
	@Override
    public ActionGroup getBundleActionGroup() {
		loadBundlesIfNeeded();
		
		ActionGroup ag = new ActionGroup();
		for (String key : bundles.keySet()) {
			if (key == null) {
				buildMenu(ag, bundles.get(key));
			} else {
				ActionGroup sub = new ActionGroup(key);
				ag.add(sub);
				buildMenu(sub, bundles.get(key));
			}
		}
		return ag;
    }

	@Override
	@Deprecated
    public Map<String, List<Bundle>> getBundles() {
		loadBundlesIfNeeded();
	    return bundles;
    }

    public Map<String, Map<ScopeSelector, Object>> getPreferences() {
    	return preferences;
    }
    
	@Override
    public Object getPreference(String key, Scope scope) {
		Map<ScopeSelector, Object> prefs = preferences.get(key);
		return new ScopeSelectorManager().getMatch(scope, prefs);
    }
	
	private void buildMenu(ActionGroup ag, List<Bundle> list) {
	    for (Bundle b : list) {
	    	ag.add(b.getMenu());
	    }
    }
	
	private synchronized void loadBundlesIfNeeded() {
		if (bundles != null) return;
		reload(new CachingPListReader(true));
    }

    public void reload() {
        reload(new CachingPListReader(false));
    }

	public void reload(CachingPListReader r) {
	    bundles = Maps.newHashMap();
		List<Bundle> bundleList = Lists.newArrayList();
		bundles.put(null, bundleList);

		PerformanceLogger.get().enter(this, "reload.load");

		File[] bundles = new File(bundleDir).listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
		Arrays.sort(bundles);
	    for (File bundleDir : bundles) {
	    	try {
	    		bundleList.add(buildFromDirectory(r, bundleDir));
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	    
	    PerformanceLogger.get().exit();
	    
	    
		PerformanceLogger.get().enter(this, "reload.index");
	    
	    preferences = Maps.newHashMap();
	    for (Bundle b : bundleList) {
	    	for (Map.Entry<String, Map<ScopeSelector, Object>> entry : b.getPreferences().entrySet()) {
	    		if (! preferences.containsKey(entry.getKey())) {
	    			preferences.put(entry.getKey(), entry.getValue());
	    		} else {
	    			preferences.get(entry.getKey()).putAll(entry.getValue());
	    		}
	    	}
	    }
	    
	    PerformanceLogger.get().exit();

        r.close();
    }


	
	private Bundle buildFromDirectory(PListReader r, File dir) {
		try {
	    	File bundleFile = new File(dir, "info.plist");
			Map m = (Map) r.read(bundleFile); 

			String name = (String) m.get("name");
			String defaultScopeSelector = (String) m.get("defaultScopeSelector");
		
			Map menu = (Map) m.get("mainMenu");
			if (menu == null) {
				menu = Maps.newHashMap();
			}
			
			List items = (List) menu.get("items");
			Map submenus = (Map) menu.get("submenus");

			Map<String, BundleItemSupplier> uuidToItem = Maps.newHashMap();
	    	load(new File(dir, "Commands"), r, uuidToItem);
	    	load(new File(dir, "Snippets"), r, uuidToItem);
	    	load(new File(dir, "Macros"), r, uuidToItem);

	    	Map<String, Map<ScopeSelector, Object>> preferences = Maps.newHashMap();
	    	loadPreferences(new File(dir, "Preferences"), r, preferences);

	    	
	    	ActionGroup root = new ActionGroup(name); 
	    	buildMenu(root, items, uuidToItem, submenus);

			return new Bundle(name, root, preferences, Lists.newArrayList(uuidToItem.values()));
		} catch (IOException e) {
        	throw new RuntimeException(e);
        } 
	}
	
	private void buildMenu(ActionGroup group, List items, Map<String, BundleItemSupplier> uuidToItem, Map submenus) {
		if (items == null) return;
    	for (String s : (List<String>) items) {
    		BundleItemSupplier ref = uuidToItem.get(s);
    		Map submenu = (Map) submenus.get(s);
    		if (ref != null) {
    			group.add(new BundleAction(ref));
    		} else if (submenu != null) {
    			ActionGroup sub = new ActionGroup((String) submenu.get("name"));
    			buildMenu(sub, (List) submenu.get("items"), uuidToItem, submenus);
    			group.add(sub);
    		} else if (s.startsWith("-----")) {
    			group.add(null);
    		}
    	}
    }

	private void loadPreferences(File file, PListReader r, 
			Map<String, Map<ScopeSelector, Object>> prefs) throws FileNotFoundException, IOException {
		if (! file.exists()) return;
		
		for (File f : file.listFiles()) {
			String n = f.getName();
			if (n.equals("info.plist")) continue;
			if (n.endsWith(".tmPreferences") || n.endsWith(".plist")) {
				Map<String, Object> m = (Map<String, Object>) r.read(f);
				for (Map.Entry<String, Object> entry : ((Map<String, Object>) m.get("settings")).entrySet()) {
					
					// TODO: Check this
					if (m.get("scope") == null) continue;
					
					if (! prefs.containsKey(entry.getKey())) {
						prefs.put((String) entry.getKey(), new HashMap<ScopeSelector, Object>());
					}
					
					prefs.get((String) entry.getKey()).put(
							ScopeSelector.parse((String) m.get("scope")),
							entry.getValue()
					);
				}
			} 
		}
	}

	
	private void load(File dir, PListReader reader, Map<String, BundleItemSupplier> uuidToItem) throws FileNotFoundException, IOException {
		if (! dir.exists()) return;
		
		for (File file : dir.listFiles()) {
			String n = file.getName();
			if (n.equals("info.plist")) continue;
			if (n.endsWith(".plist") || n.endsWith(".tmLanguage") || n.endsWith(".tmSnippet") || 
					n.endsWith(".tmCommand")) {

				Map data = (Map) reader.read(file);
				
				String tabTrigger = (String) data.get("tabTrigger");
				String keyEq = (String) data.get("keyEquivalent");
				String scope = (String) data.get("scope");
				
				KeyStroke ks = null;
				if (keyEq != null && ! "".equals(keyEq)) {
					ks = new KeystrokeParser().parse(keyEq);
				}
				
				uuidToItem.put((String) data.get("uuid"), 
						new BundleItemSupplier(
								file, (String) data.get("name"), 
								new Activator(ks, tabTrigger, 
									scope != null ? ScopeSelector.parse(scope) : null	
								)));
			} 
		}
	}

	

	@Override
    public Collection<BundleItemSupplier> getItemsForShortcut(KeyEvent ks, Scope scope) {
		List<BundleItemSupplier> dest = Lists.newArrayList();
	    for (Map.Entry<String, List<Bundle>> e : bundles.entrySet()) {
	    	for (Bundle b : e.getValue()) {
	    		for (BundleItemSupplier ref : b.getItems()) {
	    			if (ref.getActivator().matches(ks)) {
	    				dest.add(ref);
	    			}
	    		}
	    	}
	    }
	    
	    if (dest.isEmpty()) return dest;
	    
	    ScopeSelectorManager scopeSelector = new ScopeSelectorManager();
	    return scopeSelector.getAllMatches(scope, dest, new ScopeSelectorManager.ScopeSelectorExtractor<BundleItemSupplier>() {
            public ScopeSelector getScopeSelector(BundleItemSupplier t) {
	            return t.getActivator().getScopeSelector();
            }
	    });
    }

	@Override
    public Collection<BundleItemSupplier> getItemsForTabTrigger(String trigger, Scope scope) {
		List<BundleItemSupplier> dest = Lists.newArrayList();
	    for (Map.Entry<String, List<Bundle>> e : bundles.entrySet()) {
	    	for (Bundle b : e.getValue()) {
	    		for (BundleItemSupplier ref : b.getItems()) {
	    			if (ref.getActivator().matches(trigger)) {
	    				dest.add(ref);
	    			}
	    		}
	    	}
	    }
	    
	    if (dest.isEmpty()) return dest;
	    
	    ScopeSelectorManager scopeSelector = new ScopeSelectorManager();
	    return scopeSelector.getAllMatches(scope, dest, new ScopeSelectorManager.ScopeSelectorExtractor<BundleItemSupplier>() {
            public ScopeSelector getScopeSelector(BundleItemSupplier t) {
	            return t.getActivator().getScopeSelector();
            }
	    });
    }



    static class CachingPListReader implements PListReader, Serializable {
        private Map<String, Object> cache;
        private transient PListReader delegate;
        private boolean loadFromDisk;

        public CachingPListReader(boolean loadFromDisk) {
            this.loadFromDisk = loadFromDisk;
            if (loadFromDisk) {
                cache = (Map<String, Object>) Application.get().getPersistenceManager().load("bundle.cache");
                if (cache == null) {
                    cache = Maps.newHashMap();
                }
            } else {
                cache = Maps.newHashMap();
            }
        }

        @Override
        public Object read(File file) throws IOException {
            if (delegate == null) delegate = new GeneralPListReader();

            String key = file.toString();
            if (cache.containsKey(key)) return cache.get(key);

            Object o = delegate.read(file);
            cache.put(key, o);
            return o;
        }

        public void close() {
            if (! loadFromDisk)
                Application.get().getPersistenceManager().save("bundle.cache", cache);
            cache.clear();
        }
    }

}
