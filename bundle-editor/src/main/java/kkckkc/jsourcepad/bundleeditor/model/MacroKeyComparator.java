package kkckkc.jsourcepad.bundleeditor.model;

import com.google.common.collect.Maps;

import java.util.Comparator;
import java.util.Map;

public class MacroKeyComparator implements Comparator<String> {
    static Map<String, Integer> fixedOrdering;
    static {
        int i = 0;
        fixedOrdering = Maps.newHashMap();
        fixedOrdering.put("command", ++i);
        fixedOrdering.put("arguments", ++i);
    }

    @Override
    public int compare(String o1, String o2) {
        Integer i1 = fixedOrdering.get(o1);
        Integer i2 = fixedOrdering.get(o2);

        if (i1 != null && i2 != null) {
            return i1.compareTo(i2);
        }
        if (i1 != null) {
            return -1;
        }
        if (i2 != null) {
            return 1;
        }

        return o1.compareTo(o2);
    }
}
