package kkckkc.jsourcepad;

import com.google.common.base.Objects;

import java.util.*;

public class PluginManager {
    private static List<Plugin> plugins;
    private static List<Plugin> allPlugins;

    public static Iterable<Plugin> getActivePlugins() {
        if (plugins == null) {
            plugins = loadPlugins(true);
        }
        return plugins;
    }

    private static synchronized List<Plugin> loadPlugins(boolean filter) {
        ServiceLoader<Plugin> loader = ServiceLoader.load(Plugin.class);

        List<Plugin> unresolved = new ArrayList<Plugin>();
        for (Plugin p : loader) {
            if (! filter || p.isEnabled()) unresolved.add(p);
        }

        Map<String, Plugin> resolved = new LinkedHashMap<String, Plugin>();

        // Loop until there are no more entries being removed from the list of unresolved plugins
        int lastNumberOfUnresolved = -1;
        while (unresolved.size() != lastNumberOfUnresolved) {
            lastNumberOfUnresolved = unresolved.size();

            Iterator<Plugin> it = unresolved.iterator();
            while (it.hasNext()) {
                Plugin plugin = it.next();

                String[] dependencies = Objects.firstNonNull(plugin.getDependsOn(), new String[] {});

                boolean allDependenciesAreResolved = true;
                for (String id : dependencies) {
                    allDependenciesAreResolved &= resolved.containsKey(id);
                }

                if (allDependenciesAreResolved) {
                    resolved.put(plugin.getId(), plugin);
                    it.remove();
                }
            }
        }

        if (! unresolved.isEmpty()) {
            throw new RuntimeException("Cannot load plugins due to dependency problems");
        }

        return new ArrayList<Plugin>(resolved.values());
    }

    public static Iterable<Plugin> getAllPlugins() {
        if (allPlugins == null) {
            allPlugins = loadPlugins(false);
        }
        return allPlugins;
    }
}
