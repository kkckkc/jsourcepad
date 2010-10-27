package kkckkc.utils.plist;

import kkckkc.utils.DomUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XMLPListWriter {
    private Document document;

    public XMLPListWriter() {
        this.document = DomUtil.newDocument();
    }

    public void setPropertyList(Object o) {
        Element e = document.createElement("plist");
        e.setAttribute("version", "1.0");
        document.appendChild(e);
        write(e, o);
    }

    private void write(Element parent, Object o) {
        if (o instanceof Map) {
            write(parent, (Map) o);
        } else if (o instanceof List) {
            write(parent, (List) o);
        } else if (o instanceof String) {
            write(parent, (String) o);
        } else {
            throw new RuntimeException("Unsupported type " + o.getClass());
        }
    }

    private void write(Element parent, Map o) {
        Element dict = document.createElement("dict");
        parent.appendChild(dict);

        for (Map.Entry entry : (Set<Map.Entry>) o.entrySet()) {
            Element key = document.createElement("key");
            key.setTextContent((String) entry.getKey());
            dict.appendChild(key);

            write(dict, entry.getValue());
        }
    }

    private void write(Element parent, List o) {
        Element array = document.createElement("array");
        parent.appendChild(array);
        for (Object obj : o) {
            write(array, obj);
        }
    }

    private void write(Element parent, String s) {
        Element string = document.createElement("string");
        string.setTextContent(s);
        parent.appendChild(string);
    }

    public Document getDocument() {
        return document;
    }

    public String getString() {
        try {
            // Create a transformer
            Transformer xformer = TransformerFactory.newInstance().newTransformer();

            // Set the public and system id
            xformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//Apple//DTD PLIST 1.0//EN");
            xformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://www.apple.com/DTDs/PropertyList-1.0.dtd");

            StringWriter dest = new StringWriter();

            // Write the DOM document to a file
            Source source = new DOMSource(document);
            Result result = new StreamResult(dest);
            xformer.transform(source, result);

            return dest.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
