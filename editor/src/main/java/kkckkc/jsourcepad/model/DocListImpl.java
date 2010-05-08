package kkckkc.jsourcepad.model;

import java.io.File;
import java.io.IOException;
import java.util.List;

import kkckkc.jsourcepad.theme.Theme;
import kkckkc.jsourcepad.util.BeanFactoryLoader;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

public class DocListImpl implements DocList {

	private int activeIndex = -1;
	private List<Doc> docs = Lists.newArrayList();

	// Collaborator
	private Window window;
	private BeanFactoryLoader beanFactoryLoader;
	
	@Autowired
	public void setWindow(Window window) {
	    this.window = window;
    }
	
	@Autowired
	public void setBeanFactoryLoader(BeanFactoryLoader beanFactoryLoader) {
	    this.beanFactoryLoader = beanFactoryLoader;
    }

	
	@Override
	public Doc create() {
		Doc doc = createDoc();
		docs.add(doc);
		window.topic(Listener.class).post().created(doc);
		setActive(docs.size() - 1);
		return doc;
	}

	private Doc createDoc() {
		BeanFactory container = beanFactoryLoader.load(BeanFactoryLoader.DOCUMENT, window);
		return container.getBean(Doc.class);
    }

	@Override
	public Iterable<Doc> getDocs() {
		return Lists.newArrayList(docs);
	}

	@Override
	public Doc open(File file) {
		int i = 0;
		for (Doc b : docs) {
			if (file.equals(b.getFile())) {
				setActive(i);
				return b;
			}
			i++;
		}
		
		Doc doc = createDoc();
		try {
			doc.open(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		docs.add(doc);
		window.topic(Listener.class).post().created(doc);
		setActive(docs.size() - 1);
		return doc;
	}

	@Override
	public Window getWindow() {
		return window;
	}

	@Override
	public int getActive() {
		return activeIndex;
	}

	@Override
	public Doc getActiveDoc() {
		if (docs.isEmpty()) return null;
		return docs.get(activeIndex);
	}

	@Override
	public void setActive(int selectedIndex) {
		if (activeIndex != selectedIndex) {
			this.activeIndex = selectedIndex;
			if (this.activeIndex >= 0) {
				Doc b = docs.get(this.activeIndex);
				b.activate();
				window.topic(Listener.class).post().selected(selectedIndex, b);
			}
		}
	}

	@Override
	public void close(Doc doc) {
		int i = 0;
		for (Doc b : docs) {
			if (b == doc) {
				break;
			}
			i++;
		}

		docs.remove(doc);
		window.topic(Listener.class).post().closed(i, doc);
	}

	@Override
	public int getDocCount() {
	    return docs.size();
	}
}
