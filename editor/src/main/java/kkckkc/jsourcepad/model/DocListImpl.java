package kkckkc.jsourcepad.model;

import com.google.common.collect.Lists;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
		Doc doc = createDoc(null);
		docs.add(doc);
		window.topic(Listener.class).post().created(doc);
		setActive(docs.size() - 1);
		return doc;
	}

	private Doc createDoc(@Nullable File file) {
		BeanFactory container = beanFactoryLoader.load(BeanFactoryLoader.DOCUMENT, window, file, null);
		return container.getBean(Doc.class);
    }

	@Override
	public Iterable<Doc> getDocs() {
		return Lists.newArrayList(docs);
	}

	@Override
	public Doc open(File file) {
        if (docs.size() == 1 && ! docs.get(0).isBackedByFile() && ! docs.get(0).isModified()) {
            close(docs.get(0));
        }

		int i = 0;
		for (Doc doc : docs) {
			if (file.equals(doc.getFile())) {
				setActive(i);
				return doc;
			}
			i++;
		}

		Doc doc = createDoc(file);
		try {
			doc.open(file);
		} catch (IOException e) {
			// Ignore
            // TODO: Logging
            System.err.println("Cannot open " + file);
            return null;
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
    public int getIndex(Doc docToFind) {
        int i = 0;
        for (Doc doc : docs) {
            if (doc == docToFind) return i;
            i++;
        }
        return -1;
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
				Doc doc = docs.get(this.activeIndex);
				doc.activate();
				window.topic(Listener.class).post().selected(selectedIndex, doc);
			}
		}
	}

    @Override
    public void setActive(Doc doc) {
        int idx = 0;
        for (Doc d : docs) {
            if (d == doc) break;
            idx++;
        }

        setActive(idx);
    }

    @Override
	public void close(Doc docToClose) {
		int i = 0;
		for (Doc doc : docs) {
			if (doc == docToClose) {
				break;
			}
			i++;
		}

		docs.remove(docToClose);
		window.topic(Listener.class).post().closed(i, docToClose);
	}

    @Override
	public int getDocCount() {
	    return docs.size();
	}
}
