package kkckkc.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
	public static <T> Iterable<T> asIterable(final NodeList nl, final Class<T> clazz) {
		return new IteratorIterable<T>(
			new Iterator<T>() {
				int position = 0;
				
				public boolean hasNext() {
					return position < nl.getLength();
				}

				public T next() {
					T t = clazz.cast(nl.item(position));
					position++;
					return t;
				}

				public void remove() { }
			}
		);
	}

	public static Iterable<Element> getChildren(final Element e, final String name) {
		return new IteratorIterable<Element>(
			new Iterator<Element>() {
				private Element next = findNext();
				
				private Element findNext() {
					for(Node n = (next == null ? e.getFirstChild() : next.getNextSibling());
						n != null; n = n.getNextSibling()){
						if (n.getNodeType() == Node.ELEMENT_NODE) {
							if (n.getNodeName().equals(name)) {
								next = (Element) n;
								return next;
							}
						}
					}
					return null;
				}
				
				public boolean hasNext() {
					return next != null;
				}

				public Element next() {
					Element e = next;
					next = findNext();
					return e;
				}

				public void remove() { }
			}
		);
	}

	public static Iterable<Element> getChildren(final Element e) {
		return new IteratorIterable<Element>(
			new Iterator<Element>() {
				private Element next = findNext();
				
				private Element findNext() {
					for(Node n = (next == null ? e.getFirstChild() : next.getNextSibling());
						n != null; n = n.getNextSibling()){
						if (n.getNodeType() == Node.ELEMENT_NODE) {
							next = (Element) n;
							return next;
						}
					}
					return null;
				}
				
				public boolean hasNext() {
					return next != null;
				}

				public Element next() {
					Element e = next;
					next = findNext();
					return e;
				}

				public void remove() { }
			}
		);
	}

	public static Element getChild(Element e, String name) {
		Element child = null;

		for(Node c = e.getFirstChild();
			c != null; c = c.getNextSibling()){
			if (c.getNodeType() == Node.ELEMENT_NODE) {
				Element el = (Element) c;
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

	public static String getText(Element e) {
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
		} catch (ParserConfigurationException e) { }
	}

	static byte[] bytes = "<?xml version='1.0' encoding='UTF-8'?>".getBytes();
	static EntityResolver ENTITY_RESOLVER = new EntityResolver() {
		public InputSource resolveEntity(String publicId, String systemId) {
			return new InputSource(new ByteArrayInputStream(bytes));
		}
	};

	static ConcurrentLinkedQueue<DocumentBuilder> documentBuilderPool = new ConcurrentLinkedQueue<DocumentBuilder>();
	
	public static Document parse(InputSource source) {
		try {
			DocumentBuilder builder = documentBuilderPool.poll();
		    if (builder == null) {
		        builder = factory.newDocumentBuilder();
                builder.setEntityResolver(ENTITY_RESOLVER);
		    }

			Document d = builder.parse(source);

			builder.reset();
			documentBuilderPool.offer(builder);
			
			return d;
		} catch (IOException e) {
			throw new RuntimeException("Can't parse", e);
		} catch (SAXException e) {
			throw new RuntimeException("Can't parse", e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Can't create parser", e);
		}
	}

	public static Document parse(File file) throws FileNotFoundException {
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
}
