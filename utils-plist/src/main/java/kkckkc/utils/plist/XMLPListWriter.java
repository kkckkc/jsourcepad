package kkckkc.utils.plist;

import nanoxml.XMLElement;

import java.util.List;
import java.util.Map;

public class XMLPListWriter {
    private XMLElement root;

    public XMLPListWriter() {
    }

    public void setPropertyList(Object o) {
        this.root = new XMLElement();
        this.root.setName("plist");
        this.root.setAttribute("version", "1.0");

        write(this.root, o);
    }

    private void write(XMLElement parent, Object o) {
        if (o instanceof Map) {
            write(parent, (Map<String, Object>) o);
        } else if (o instanceof List) {
            write(parent, (List) o);
        } else if (o instanceof String) {
            write(parent, (String) o);
        } else if (o instanceof Boolean) {
            write(parent, (Boolean) o);
        } else if (o instanceof Integer) {
            write(parent, (Integer) o);
        } else {
            throw new RuntimeException("Unsupported type " + o.getClass());
        }
    }

    private void write(XMLElement parent, Map<String, Object> o) {
        XMLElement dict = new XMLElement();
        dict.setName("dict");
        parent.addChild(dict);

        for (Map.Entry<String, Object> entry : o.entrySet()) {
            XMLElement key = new XMLElement();
            key.setName("key");
            key.setContent(entry.getKey());
            dict.addChild(key);

            write(dict, entry.getValue());
        }
    }

    private void write(XMLElement parent, List o) {
        XMLElement array = new XMLElement();
        array.setName("array");
        parent.addChild(array);
        for (Object obj : o) {
            write(array, obj);
        }
    }

    private void write(XMLElement parent, String s) {
        XMLElement stringElement = new XMLElement();
        stringElement.setName("stringElement");
        stringElement.setContent(s);
        parent.addChild(stringElement);
    }

    private void write(XMLElement parent, Integer i) {
        XMLElement integerElement = new XMLElement();
        integerElement.setName("integer");
        integerElement.setContent(i.toString());
        parent.addChild(integerElement);
    }

    private void write(XMLElement parent, Boolean boolenValue) {
        XMLElement booleanElement = new XMLElement();
        if (boolenValue) {
            booleanElement.setName("true");
        } else {
            booleanElement.setName("false");
        }
        parent.addChild(booleanElement);
    }

    public String getString() {
        return
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                this.root.toString().replaceAll("VERSION=\"1\\.0\"", "version=\"1.0\"");
    }
}
