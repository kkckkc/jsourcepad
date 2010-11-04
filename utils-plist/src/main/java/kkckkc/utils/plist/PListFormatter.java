package kkckkc.utils.plist;

import java.util.*;

public class PListFormatter {

    Comparator<String> mapKeyComparator;

    public void setMapKeyComparator(Comparator<String> mapKeyComparator) {
        this.mapKeyComparator = mapKeyComparator;
    }

    public void format(Object o, StringBuilder dest) {
		format(o, dest, 0, true);
	}

	private void format(Object o, StringBuilder dest, int i, boolean shortNotation) {
	    if (o instanceof Map) {
	    	formatMap((Map<String, Object>) o, dest, i, shortNotation);
	    } else if (o instanceof List) {
	    	formatList((List) o, dest, i, shortNotation);
	    } else if (o instanceof String) {
	    	dest.append("\"").append(((String) o).replaceAll("\"", "\\\\\"")).append("\"");
	    } else if (o instanceof Number) {
	    	dest.append(o);
	    } else if (o instanceof Boolean) {
	    	dest.append(o);
	    } else {
	    	System.out.println("Unsupported: " + o.getClass());
	    }
    }

	private void formatList(List arr, StringBuilder dest, int i, boolean shortNotation) {
        // TODO: Potential one line notation for lists with only string / integers when length is short enough

        if (arr.size() == 1 && ! (arr.get(0) instanceof Map || arr.get(0) instanceof List)) {
            dest.append("( ");
            if (! shortNotation) dest.append("  ");
            for (Object o : arr) {
                format(o, dest, i + 1, true);
            }
            dest.append(" )");
        } else {
            dest.append("(\n");
            for (Object o : arr) {
                StringBuilder sb = new StringBuilder();
                sb.append(indent(i + 1));
                format(o, sb, i + 1, false);
                sb.append(",\n");

                dest.append(sb);
            }
            dest.append(indent(i)).append(")");
        }
    }

	private void formatMap(Map<String, Object> map, StringBuilder dest, int i, boolean shortNotation) {
        if (map.size() == 1 && ! (map.values().iterator().next() instanceof Map || map.values().iterator().next() instanceof List)) {
            dest.append("{ ");
            if (! shortNotation) dest.append("  ");
            for (Map.Entry<String, Object> e : map.entrySet()) {
                dest.append(e.getKey()).append(" = ");
                format(e.getValue(), dest, i + 1, true);
                dest.append("; ");
            }
            dest.append("}");
        } else {
		    dest.append("{   ");
            int line = 0;

            if (dest.indexOf("=") >= 0) dest.append("\n");

            if (mapKeyComparator == null) {
                for (Map.Entry<String, Object> e : map.entrySet()) {
                    formatMapEntry(dest, i, line, e.getKey(), e.getValue());
                    line++;
                }
            } else {
                List<String> keys = new ArrayList<String>(map.keySet());
                Collections.sort(keys, mapKeyComparator);

                for (String key : keys) {
                    formatMapEntry(dest, i, line, key, map.get(key));
                    line++;
                }
            }
            dest.append(indent(i)).append("}");
        }
    }

    private void formatMapEntry(StringBuilder dest, int i, int line, Object key, Object value) {
        if (line > 0 || dest.charAt(dest.length() - 1) == '\n') dest.append(indent(i + 1));

        dest.append(key).append(" = ");
        format(value, dest, i + 1, false);
        dest.append(";\n");
    }


    private String indent(int i) {
	    StringBuilder buf = new StringBuilder();
		for (int j = 0; j < i; j++) {
			buf.append("    ");
		}
		return buf.toString();
    }

	public String format(Object read) {
	    StringBuilder buf = new StringBuilder();
	    format(read, buf);
	    return buf.toString();
    }
}
