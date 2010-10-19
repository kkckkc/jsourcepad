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
	    	formatMap((Map) o, dest, i, shortNotation);
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

	private void formatMap(Map m, StringBuilder dest, int i, boolean shortNotation) {
        if (m.size() == 1 && ! (m.values().iterator().next() instanceof Map || m.values().iterator().next() instanceof List)) {
            dest.append("{ ");
            if (! shortNotation) dest.append("  ");
            Set<Map.Entry> s = m.entrySet();
            for (Map.Entry e : s) {
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
                Set<Map.Entry> s = m.entrySet();
                for (Map.Entry e : s) {
                    formatMapEntry(dest, i, line, e.getKey(), e.getValue());
                    line++;
                }
            } else {
                List<String> keys = new ArrayList(m.keySet());
                Collections.sort(keys, mapKeyComparator);

                for (String key : keys) {
                    formatMapEntry(dest, i, line, key, m.get(key));
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
	    StringBuilder b = new StringBuilder();
		for (int j = 0; j < i; j++) {
			b.append("    ");
		}
		return b.toString();
    }

	public String format(Object read) {
	    StringBuilder b = new StringBuilder();
	    format(read, b);
	    return b.toString();
    }



    public static void main(String... args) {
        Map m = new HashMap();
        m.put("a", "kalle");

        Map m2 = new HashMap();
        m.put("o", m2);

        m2.put("k", "olle");
        m2.put("j", "olle");

        Object o = m;

        PListFormatter formatter = new PListFormatter();
        System.out.println(formatter.format(o));
    }
}
