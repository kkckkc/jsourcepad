package kkckkc.utils;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class LruMap<K, V> extends LinkedHashMap<K, V> implements Serializable {
    private int maxEntries;

    public LruMap(int maxEntries) {
        this.maxEntries = maxEntries;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxEntries;
    }
}
