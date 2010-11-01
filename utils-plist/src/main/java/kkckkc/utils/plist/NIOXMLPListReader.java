package kkckkc.utils.plist;

import nanoxml.XMLElement;

import java.io.IOException;
import java.util.*;


public class NIOXMLPListReader {

	public Object read(byte[] bytes) throws IOException {
        XMLElement xmlElement = new XMLElement();
        xmlElement.parseString(new String(bytes, "utf-8"));

        for (XMLElement xe : ((Collection<XMLElement>) xmlElement.getChildren())) {
            return parseElement(xe);
        }
		throw new RuntimeException("Cannot parse file, no root element");
	}

	private Object parseElement(XMLElement e) {
		String name = e.getName();
		if ("dict".equals(name)) {
			return parseDictionary(e);
		} else if ("string".equals(name)) {
			return parseString(e);
        } else if ("true".equals(name)) {
            return Boolean.TRUE;
        } else if ("false".equals(name)) {
            return Boolean.FALSE;
        } else if ("integer".equals(name)) {
            return parseInteger(e);
		} else if ("array".equals(name)) {
			return parseArray(e);
		}
		throw new RuntimeException("Unsupported tag " + name);
	}

	private List<Object> parseArray(XMLElement e) {
		List<Object> l = new ArrayList<Object>();
        for (XMLElement xe : ((Collection<XMLElement>) e.getChildren())) {
            l.add(parseElement(xe));
        }
		return l;
	}

    private Integer parseInteger(XMLElement e) {
        return new Integer(e.getContent());
    }

	private String parseString(XMLElement e) {
		return e.getContent();
	}

	private Map<Object, Object> parseDictionary(XMLElement e) {
		Map<Object, Object> m = new LinkedHashMap<Object, Object>();
		
		String key = null;
		for (XMLElement c : ((Collection<XMLElement>) e.getChildren())) {
			if ("key".equals(c.getName())) {
				key = c.getContent();
			} else {
				m.put(key, parseElement(c));
			}
		}
		return m;
	}
}
