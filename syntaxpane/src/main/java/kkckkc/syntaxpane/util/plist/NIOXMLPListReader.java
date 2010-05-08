package kkckkc.syntaxpane.util.plist;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kkckkc.syntaxpane.util.DomUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;


public class NIOXMLPListReader {

	public Object read(byte[] bytes) throws IOException {
		Document document = DomUtil.parse(new InputSource(new ByteArrayInputStream(bytes)));
		for (Element e : DomUtil.getChildren(document.getDocumentElement())) {
			return parseElement(e);
		}
		throw new RuntimeException("Cannot parse file, no root element");
	}

	private Object parseElement(Element e) {
		String name = e.getNodeName();
		if ("dict".equals(name)) {
			return parseDictionary(e);
		} else if ("string".equals(name)) {
			return parseString(e);
		} else if ("array".equals(name)) {
			return parseArray(e);
		}
		throw new RuntimeException("Unsupported tag " + name);
	}

	private List<Object> parseArray(Element e) {
		List<Object> l = new ArrayList<Object>();
		for (Element c : DomUtil.getChildren(e)) {
			l.add(parseElement(c));
		}
		return l;
	}

	private String parseString(Element e) {
		return DomUtil.getText(e);
	}

	private Map<Object, Object> parseDictionary(Element e) {
		Map<Object, Object> m = new LinkedHashMap<Object, Object>();
		
		String key = null;
		for (Element c : DomUtil.getChildren(e)) {
			if ("key".equals(c.getNodeName())) {
				key = DomUtil.getText(c);
			} else {
				m.put(key, parseElement(c));
			}
		}
		return m;
	}
}
