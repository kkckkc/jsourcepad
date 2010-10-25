package kkckkc.jsourcepad.model.bundle;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import kkckkc.jsourcepad.model.Application;
import kkckkc.syntaxpane.model.Scope;
import kkckkc.syntaxpane.parse.grammar.textmate.TextmateScopeSelectorParser;
import kkckkc.syntaxpane.style.ScopeSelector;
import kkckkc.syntaxpane.style.ScopeSelectorManager;
import kkckkc.utils.Pair;
import kkckkc.utils.PerformanceLogger;
import kkckkc.utils.io.FileUtils;
import kkckkc.utils.plist.GeneralPListReader;
import kkckkc.utils.plist.PListReader;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;

public class BundleManagerImpl implements BundleManager {

    private List<Listener> listeners = Lists.newArrayList();
	private List<Bundle> bundles;
	private Map<String, Map<ScopeSelector, Object>> preferences;

	private String bundleDir;
	
	public BundleManagerImpl(String bundleDir) {
		this.bundleDir = FileUtils.expandAbbreviations(bundleDir);
	}

    @Override
    public File getBundleDir() {
        return new File(bundleDir);
    }

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
	@Deprecated
    public List<Bundle> getBundles() {
		loadBundlesIfNeeded();
	    return bundles;
    }

    @Override
    public Bundle getBundle(String name) {
        for (Bundle b : bundles) {
            if (b.getName().equals(name)) return b;
        }
        return null;
    }


    @Override
    public Bundle getBundle(File dir) {
        for (Bundle b : bundles) {
            if (b.getDir().equals(dir)) return b;
        }
        return null;
    }

    public Map<String, Map<ScopeSelector, Object>> getPreferences() {
    	return preferences;
    }
    
	@Override
    public Object getPreference(String key, Scope scope) {
		Map<ScopeSelector, Object> prefs = preferences.get(key);
		return new ScopeSelectorManager().getMatch(scope, prefs);
    }

    private synchronized void loadBundlesIfNeeded() {
		if (bundles != null) return;
		reload(new CachingPListReader(true));
    }

    public synchronized void reload() {
        reload(new CachingPListReader(false));
    }

    @Override
    public synchronized void reload(Bundle bundle) {
        bundles.remove(bundle);
        bundle = buildFromDirectory(new CachingPListReader(true), bundle.getDir());
        bundles.add(bundle);

        indexPreferences();
        for (Listener l : listeners) l.bundleUpdated(bundle);
    }

    @Override
    public synchronized void addBundle(File dir) {
        Bundle bundle = buildFromDirectory(new CachingPListReader(true), dir);
        bundles.add(bundle);

        indexPreferences();
        for (Listener l : listeners) l.bundleAdded(bundle);
    }

    @Override
    public synchronized void remove(Bundle bundle) {
        bundles.remove(bundle);
        indexPreferences();
        for (Listener l : listeners) l.bundleRemoved(bundle);
    }

    public void reload(CachingPListReader r) {
        List<Bundle> oldBundles = bundles;
	    bundles = Lists.newArrayList();

		PerformanceLogger.get().enter(this, "reload.load");

		File[] bundles = new File(bundleDir).listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
		Arrays.sort(bundles);
	    for (File bundleDir : bundles) {
	    	try {
                Bundle newBundle = buildFromDirectory(r, bundleDir);
                this.bundles.add(newBundle);

                if (oldBundles != null) {
                    boolean found = false;
                    for (Bundle b : oldBundles) {
                        if (b.getDir().equals(bundleDir)) {
                            for (Listener l : listeners) l.bundleUpdated(newBundle);
                            found = true;
                            break;
                        }
                    }
                    if (! found) for (Listener l : listeners) l.bundleAdded(newBundle);
                }
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	    
	    PerformanceLogger.get().exit();
	    

        if (oldBundles != null) {
            for (Bundle b : oldBundles) {
                boolean found = false;
                for (Bundle nb : this.bundles) {
                    if (nb.getDir().equals(b)) {
                        found = true;
                    }
                }
                if (! found) for (Listener l : listeners) l.bundleRemoved(b);
            }
        }


        PerformanceLogger.get().enter(this, "reload.index");
        indexPreferences();
        PerformanceLogger.get().exit();

        r.close();
    }

    private void indexPreferences() {
        preferences = Maps.newHashMap();
        for (Bundle b : bundles) {
            for (Map.Entry<String, Map<ScopeSelector, Object>> entry : b.getPreferences().entrySet()) {
                if (! preferences.containsKey(entry.getKey())) {
                    preferences.put(entry.getKey(), entry.getValue());
                } else {
                    preferences.get(entry.getKey()).putAll(entry.getValue());
                }
            }
        }
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
	    	load(dir, BundleStructure.Type.COMMAND, r, uuidToItem);
	    	load(dir, BundleStructure.Type.SNIPPET, r, uuidToItem);
	    	load(dir, BundleStructure.Type.MACRO, r, uuidToItem);
            loadTemplates(dir, BundleStructure.Type.TEMPLATE, r, uuidToItem);

	    	Map<String, Map<ScopeSelector, Object>> preferences = Maps.newHashMap();
	    	loadPreferences(dir, BundleStructure.Type.PREFERENCE, r, preferences);

            List<Object> root = Lists.newArrayList();
	    	buildMenu(root, items, uuidToItem, submenus);

			return new Bundle(name, root, preferences, uuidToItem, dir);
		} catch (IOException e) {
        	throw new RuntimeException(e);
        } 
	}
	
	private void buildMenu(List<Object> root, List items, Map<String, BundleItemSupplier> uuidToItem, Map submenus) {
		if (items == null) return;
    	for (String s : (List<String>) items) {
    		BundleItemSupplier ref = uuidToItem.get(s);
    		Map submenu = (Map) submenus.get(s);
    		if (ref != null) {
                root.add(ref);
    		} else if (submenu != null) {
                Pair<String, List<Object>> sub = new Pair<String, List<Object>>((String) submenu.get("name"), Lists.<Object>newArrayList());
    			buildMenu(sub.getSecond(), (List) submenu.get("items"), uuidToItem, submenus);
    			root.add(sub);
    		} else if (s.startsWith("-----")) {
                root.add(null);
    		}
    	}
    }

	private void loadPreferences(File dir, BundleStructure.Type type, PListReader r,
			Map<String, Map<ScopeSelector, Object>> prefs) throws FileNotFoundException, IOException {
        dir = new File(dir, type.getFolder());
		if (! dir.exists()) return;
		
		for (File f : dir.listFiles()) {
			String n = f.getName();
			if (n.equals("info.plist")) continue;
			if (BundleStructure.isOfType(type, f)) {
				Map<String, Object> m = (Map<String, Object>) r.read(f);
				for (Map.Entry<String, Object> entry : ((Map<String, Object>) m.get("settings")).entrySet()) {
					
					// TODO: Check this
					if (m.get("scope") == null) continue;
					
					if (! prefs.containsKey(entry.getKey())) {
						prefs.put(entry.getKey(), new HashMap<ScopeSelector, Object>());
					}
					
					prefs.get(entry.getKey()).put(
							TextmateScopeSelectorParser.parse((String) m.get("scope")),
							entry.getValue()
					);
				}
			} 
		}
	}

	
	private void load(File dir, BundleStructure.Type type, PListReader reader, Map<String, BundleItemSupplier> uuidToItem) throws FileNotFoundException, IOException {
        dir = new File(dir, type.getFolder());

		if (! dir.exists()) return;
		
		for (File file : dir.listFiles()) {
			String n = file.getName();
			if (n.equals("info.plist")) continue;
			if (BundleStructure.isOfType(type, file)) {

				Map data = (Map) reader.read(file);
				
				String tabTrigger = (String) data.get("tabTrigger");
				String keyEq = (String) data.get("keyEquivalent");
				String scope = (String) data.get("scope");
				
				KeyStroke ks = null;
				if (keyEq != null && ! "".equals(keyEq)) {
					ks = new TextmateKeystrokeEncoding().parse(keyEq);
				}

				uuidToItem.put((String) data.get("uuid"),
						new BundleItemSupplier(
								file, (String) data.get("uuid"), (String) data.get("name"), 
								new Activator(ks, tabTrigger, 
									scope != null ? TextmateScopeSelectorParser.parse(scope) : null	
								),
                                type));
			} 
		}
	}


    private void loadTemplates(File dir, BundleStructure.Type type, PListReader reader, Map<String, BundleItemSupplier> uuidToItem) throws FileNotFoundException, IOException {
        dir = new File(dir, type.getFolder());
        if (! dir.exists()) return;

        for (File subdir : dir.listFiles()) {
            if (! subdir.isDirectory()) continue;

            File file = new File(subdir, "info.plist");
            Map data = (Map) reader.read(file);

            uuidToItem.put((String) data.get("uuid"),
                        new BundleItemSupplier(
                                file, (String) data.get("uuid"), (String) data.get("name"),
                                null,
                                BundleStructure.Type.TEMPLATE));
        }
    }


	@Override
    public Collection<BundleItemSupplier> getItemsForShortcut(final KeyEvent ks, Scope scope) {
        List<BundleItemSupplier> dest = findBundleItems(new Predicate<BundleItemSupplier>() {
            public boolean apply(BundleItemSupplier bundleItemSupplier) {
                return bundleItemSupplier.getActivator().matches(ks);
            }
        }, true);

	    if (dest.isEmpty()) return dest;
	    
	    ScopeSelectorManager scopeSelector = new ScopeSelectorManager();
	    return scopeSelector.getBestMatches(scope, dest, new ScopeSelectorManager.ScopeSelectorExtractor<BundleItemSupplier>() {
            public ScopeSelector getScopeSelector(BundleItemSupplier t) {
                if (t == null) return null;
	            return t.getActivator().getScopeSelector();
            }
	    });
    }

	@Override
    public Collection<BundleItemSupplier> getItemsForTabTrigger(final String trigger, Scope scope) {
		List<BundleItemSupplier> dest = findBundleItems(new Predicate<BundleItemSupplier>() {
            public boolean apply(BundleItemSupplier bundleItemSupplier) {
                return bundleItemSupplier.getActivator().matches(trigger);
            }
        }, false);
	    if (dest.isEmpty()) return dest;

	    ScopeSelectorManager scopeSelector = new ScopeSelectorManager();
	    return scopeSelector.getBestMatches(scope, dest, new ScopeSelectorManager.ScopeSelectorExtractor<BundleItemSupplier>() {
            public ScopeSelector getScopeSelector(BundleItemSupplier t) {
	            return t.getActivator().getScopeSelector();
            }
	    });
    }


    private List<BundleItemSupplier> findBundleItems(Predicate<BundleItemSupplier> predicate, boolean delimit) {
        List<BundleItemSupplier> dest = Lists.newArrayList();
        for (Bundle b : bundles) {
            int size = dest.size();
            findInMenu(dest, b.getMenu(), predicate, delimit);
            if (delimit && size != dest.size()) dest.add(null);
        }

        return dest;
    }

    private void findInMenu(List<BundleItemSupplier> dest, List<Object> menu, Predicate<BundleItemSupplier> predicate,
                            boolean delimit) {
        for (Object o : menu) {
            if (o == null) {
                if (delimit) dest.add(null);
            } else if (o instanceof BundleItemSupplier) {
                BundleItemSupplier bis = (BundleItemSupplier) o;
                if (predicate.apply(bis)) {
                    dest.add(bis);
                }
            } else {
                Pair<String, List<Object>> pair = (Pair<String, List<Object>>) o;
                int size = dest.size();
                findInMenu(dest, pair.getSecond(), predicate, delimit);
                if (delimit && size != dest.size()) dest.add(null);
            }
        }
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
                    this.loadFromDisk = false;
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
