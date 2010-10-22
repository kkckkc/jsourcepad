package kkckkc.jsourcepad.bundleeditor.model;

import com.google.common.collect.Maps;

import java.util.Comparator;
import java.util.Map;

public class SyntaxesKeyComparator implements Comparator<String> {
    static Map<String, Integer> fixedOrdering;
    static {
        int i = 0;
        fixedOrdering = Maps.newHashMap();
        fixedOrdering.put("name", ++i);
        fixedOrdering.put("scopeName", ++i);
        fixedOrdering.put("firstLineMatch", ++i);
        fixedOrdering.put("fileTypes", ++i);
        fixedOrdering.put("foldingStartMarker", ++i);
        fixedOrdering.put("foldingStopMarker", ++i);
        fixedOrdering.put("patterns", ++i);
        fixedOrdering.put("repository", ++i);

        fixedOrdering.put("contentName", ++i);
        fixedOrdering.put("match", ++i);
        fixedOrdering.put("begin", ++i);
        fixedOrdering.put("end", ++i);
        fixedOrdering.put("captures", ++i);
        fixedOrdering.put("beginCaptures", ++i);
        fixedOrdering.put("endCaptures", ++i);
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
