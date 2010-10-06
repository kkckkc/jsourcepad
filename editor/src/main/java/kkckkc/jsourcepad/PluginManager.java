package kkckkc.jsourcepad;

import kkckkc.utils.IteratorIterable;

import java.util.*;

public class PluginManager {
    private static List<Plugin> plugins;

    public static Iterable<Plugin> getActivePlugins() {
        if (plugins == null) {
            loadPlugins();
        }
        return plugins;
    }

    private static synchronized void loadPlugins() {
        ServiceLoader<Plugin> loader = ServiceLoader.load(Plugin.class);

        List<Plugin> available = new ArrayList<Plugin>();
        for (Plugin p : new IteratorIterable<Plugin>(loader.iterator())) {
            if (p.isEnabled()) available.add(p);
        }

        Map<String, Plugin> resolved = new LinkedHashMap<String, Plugin>();

        int previousSize = -1;
        while (available.size() != previousSize) {
            previousSize = available.size();

            Iterator<Plugin> it = available.iterator();
            while (it.hasNext()) {
                Plugin plugin = it.next();
                if (plugin.getDependsOn() == null || plugin.getDependsOn().length == 0) {
                    resolved.put(plugin.getId(), plugin);
                    it.remove();
                } else {
                    boolean allResolved = true;
                    for (String id : plugin.getDependsOn()) {
                        allResolved &= resolved.containsKey(id);
                    }
                    if (allResolved) {
                        resolved.put(plugin.getId(), plugin);
                        it.remove();
                    }
                }
            }
        }

        if (! available.isEmpty()) {
            throw new RuntimeException("Cannot load plugins due to dependency problems");
        }

        plugins = new ArrayList<Plugin>();
        plugins.addAll(resolved.values());
    }
}
