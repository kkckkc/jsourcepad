package kkckkc.utils.plist;

import nanoxml.XMLElement;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class NIOXMLPListReader {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	public Object read(byte[] bytes) throws IOException {
        XMLElement xmlElement = new XMLElement();
        xmlElement.parseString(new String(bytes, "utf-8"));

        XMLElement xe = (XMLElement) xmlElement.getChildren().iterator().next();
        return parseElement(xe);
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
		} else if ("date".equals(name)) {
			return parseDate(e);
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

    private Date parseDate(XMLElement e) {
        try {
            return dateFormat.parse(e.getContent());
        } catch (ParseException e1) {
            throw new RuntimeException(e1);
        }
    }

	private Map<Object, Object> parseDictionary(XMLElement e) {
		Map<Object, Object> dict = new LinkedHashMap<Object, Object>();
		
		String key = null;
		for (XMLElement child : ((Collection<XMLElement>) e.getChildren())) {
			if ("key".equals(child.getName())) {
				key = child.getContent();
			} else {
				dict.put(key, parseElement(child));
			}
		}
		return dict;
	}
}
