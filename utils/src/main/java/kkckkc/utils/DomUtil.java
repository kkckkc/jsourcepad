package kkckkc.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DomUtil {
	public static Iterable<Element> getChildren(final Element e, final String name) {
		return new ElementChildrenIterator(e, name);
	}

	public static Iterable<Element> getChildren(final Element e) {
        return new ElementChildrenIterator(e);
	}

	public static Element getChild(@NotNull Element e, @NotNull String name) {
		Element child = null;

		for (Node node = e.getFirstChild();
			node != null; node = node.getNextSibling()){
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element el = (Element) node;
				if (el.getNodeName().equals(name)) {
					child = el;
				}
			}
		}
		return child;
	}

	public static String getChildText(Element e, String name) {
		Element child = getChild(e, name);
		if (child == null) return null;
		return getText(child);
	}

	public static String getText(@NotNull Element e) {
		StringBuilder sb = new StringBuilder(100);

		for(Node child = e.getFirstChild();
			child != null; child = child.getNextSibling()){
			if (child.getNodeType() == Node.CDATA_SECTION_NODE || child.getNodeType() == Node.TEXT_NODE) {
				sb.append(child.getTextContent());
			}
		}

		return sb.toString();
	}


	static DocumentBuilderFactory factory;
	static {
		factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(false);
		try {
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		} catch (ParserConfigurationException e) {
            // Ignore
        }
	}

	static byte[] bytes = "<?xml version='1.0' encoding='UTF-8'?>".getBytes();
	static EntityResolver ENTITY_RESOLVER = new EntityResolver() {
		public InputSource resolveEntity(String publicId, String systemId) {
			return new InputSource(new ByteArrayInputStream(bytes));
		}
	};

	static ConcurrentLinkedQueue<DocumentBuilder> documentBuilderPool = new ConcurrentLinkedQueue<DocumentBuilder>();
	
	public static Document parse(@NotNull InputSource source) {
		try {
			DocumentBuilder builder = documentBuilderPool.poll();
		    if (builder == null) {
		        builder = factory.newDocumentBuilder();
                builder.setEntityResolver(ENTITY_RESOLVER);
		    }

			Document document = builder.parse(source);

			builder.reset();
			documentBuilderPool.offer(builder);
			
			return document;
		} catch (IOException e) {
			throw new RuntimeException("Can't parse", e);
		} catch (SAXException e) {
			throw new RuntimeException("Can't parse", e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Can't create parser", e);
		}
	}

	public static Document parse(@NotNull File file) throws FileNotFoundException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		try {
			return parse(new InputSource(reader));
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				// Ignore
			}
		}
	}


    private static class ElementChildrenIterator implements Iterator<Element>, Iterable<Element> {
        private Element next;
        private final Element e;
        private final String name;

        public ElementChildrenIterator(@NotNull Element e, @Nullable String name) {
            this.e = e;
            this.name = name;
            findNext();
        }

        public ElementChildrenIterator(@NotNull Element e) {
            this(e, null);
        }

        private void findNext() {
            for(Node node = (next == null ? e.getFirstChild() : next.getNextSibling());
                node != null; node = node.getNextSibling()){
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    if (name == null || node.getNodeName().equals(name)) {
                        next = (Element) node;
                        return;
                    }
                }
            }
            next = null;
        }

        public boolean hasNext() {
            return next != null;
        }

        public Element next() {
            Element e = next;
            findNext();
            return e;
        }

        public void remove() { }

        @Override
        public Iterator<Element> iterator() {
            return this;
        }
    }
}
