package kkckkc.syntaxpane.util.plist;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PListFormatter {
	
	
	public void format(Object o, StringBuilder dest) {
		format(o, dest, 0);
	}

	private void format(Object o, StringBuilder dest, int i) {
	    if (o instanceof Map) {
	    	formatMap((Map) o, dest, i);
	    } else if (o instanceof List) {
	    	formatList((List) o, dest, i);
	    } else if (o instanceof String) {
	    	dest.append("'").append(o).append("'");
	    } else if (o instanceof Number) {
	    	dest.append(o);
	    } else if (o instanceof Boolean) {
	    	dest.append(o);
	    } else {
	    	System.out.println("Unsupported: " + o.getClass());
	    }
    }

	private void formatList(List arr, StringBuilder dest, int i) {
		dest.append("(\n");
		for (Object o : arr) {
			dest.append(indent(i + 1));
			format(o, dest, i + 1);
			dest.append(",\n");
		}
		dest.append(indent(i)).append(")");
    }

	private void formatMap(Map m, StringBuilder dest, int i) {
		dest.append("{\n");
		Set<Map.Entry> s = m.entrySet();
		for (Map.Entry e : s) {
			dest.append(indent(i + 1)).append(e.getKey()).append(" = ");
			format(e.getValue(), dest, i + 1);
			dest.append(";\n");
		}
		dest.append(indent(i)).append("}");
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
	
}
