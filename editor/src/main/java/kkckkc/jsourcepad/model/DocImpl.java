package kkckkc.jsourcepad.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import kkckkc.jsourcepad.ScopeRoot;
import kkckkc.jsourcepad.util.messagebus.AbstractMessageBus;
import kkckkc.syntaxpane.model.SourceDocument;
import kkckkc.syntaxpane.parse.grammar.Language;
import kkckkc.syntaxpane.parse.grammar.LanguageManager;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class DocImpl extends AbstractMessageBus implements Doc, ScopeRoot {
	private LanguageManager languageManager;
	private DocList docList;
	private Window window;
	private File backingFile;
	private Buffer buffer;
	private TabManager tabManager;
	private BeanFactory container;

	@Autowired
	public DocImpl(final Window window, DocList docList, LanguageManager languageManager) {
		this.docList = docList;

		this.buffer = new BufferImpl(new SourceDocument(), this, window);
		this.buffer.setLanguage(languageManager.getLanguage(null));
		
		this.window = window;
		this.languageManager = languageManager;
		this.tabManager = new TabManagerImpl(this);
	}
	
	public Buffer getActiveBuffer() {
		return buffer;
	}
	
	@Override
	public void close() {
		docList.close(this);
	}

	@Override
	public String getTitle() {
		return (isModified() ? "*" : "") + 
			(backingFile == null ? "Untitled" : backingFile.getName());
	}

	@Override
	public boolean isModified() {
		return getActiveBuffer().isModified();
	}

	@Override
	public boolean isBackedByFile() {
		return backingFile != null;
	}

	@Override
	public void save() {
		try {
			FileWriter fw = new FileWriter(this.backingFile);
			fw.write(buffer.getText(buffer.getCompleteDocument()));
			fw.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		getActiveBuffer().clearModified();
		window.topic(Doc.StateListener.class).post().modified(this);
	}

	@Override
	public void saveAs(File file) {
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(buffer.getText(buffer.getCompleteDocument()));
			fw.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		getActiveBuffer().clearModified();
		
		this.backingFile = file;
		
		window.topic(Project.FileChangeListener.class).post().created(file);
		window.topic(Doc.StateListener.class).post().modified(this);
	}

	@Override
	public void open(File file) throws IOException {
		this.backingFile = file;
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		br.mark(1024);
		
		char[] buffer = new char[800];
		br.read(buffer);
		br.reset();
		
		Language language = languageManager.getLanguage(new String(buffer), (File) file);
		
		this.buffer.setText(language, br);
	}

	
	
	@Override
	public File getFile() {
		return backingFile;
	}

	@Override
	public void activate() {
		InsertionPoint ip = this.buffer.getInsertionPoint();
		window.topic(InsertionPointListener.class).post().update(ip);
	}

	@Override
	public DocList getDocList() {
		return docList;
	}
	
	@Override
	public TabManager getTabManager() {
	    return tabManager;
	}

	@Override
    public <T> T getPresenter(Class<? extends T> clazz) {
	    return container.getBean(clazz);
    }

	@Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
	    this.container = beanFactory;
    }
	
	@Override
	public BeanFactory getBeanFactory() {
	    return container;
	}
	
}
