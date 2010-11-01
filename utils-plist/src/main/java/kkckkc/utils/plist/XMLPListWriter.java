package kkckkc.utils.plist;

import nanoxml.XMLElement;

import java.util.*;

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
            write(parent, (Map) o);
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

    private void write(XMLElement parent, Map o) {
        XMLElement dict = new XMLElement();
        dict.setName("dict");
        parent.addChild(dict);

        for (Map.Entry entry : (Set<Map.Entry>) o.entrySet()) {
            XMLElement key = new XMLElement();
            key.setName("key");
            key.setContent((String) entry.getKey());
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
        XMLElement string = new XMLElement();
        string.setName("string");
        string.setContent(s);
        parent.addChild(string);
    }

    private void write(XMLElement parent, Integer s) {
        XMLElement string = new XMLElement();
        string.setName("integer");
        string.setContent(s.toString());
        parent.addChild(string);
    }

    private void write(XMLElement parent, Boolean s) {
        if (s) {
            XMLElement string = new XMLElement();
            string.setName("true");
            parent.addChild(string);
        } else {
            XMLElement string = new XMLElement();
            string.setName("false");
            parent.addChild(string);
        }
    }

    public String getString() {
        return
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                this.root.toString().replaceAll("VERSION=\"1\\.0\"", "version=\"1.0\"");
    }

    public static void main(String... args) {
        Map m = new HashMap();
        m.put("test", Arrays.asList("Lorem", "Ipsum", Boolean.TRUE, new Integer(5)));

        XMLPListWriter xmlpListWriter = new XMLPListWriter();
        xmlpListWriter.setPropertyList(m);
        System.out.println(xmlpListWriter.getString());
    }
}
