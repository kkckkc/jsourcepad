package kkckkc.jsourcepad.model;

import java.io.File;
import java.io.IOException;


public interface Doc {

    public interface StateListener {
		public void modified(Doc doc, boolean newState, boolean oldState);
	}
	
	public DocList getDocList();

	public void open(File file) throws IOException;
	public void save();
	public void saveAs(File file);
	public void close();
	public File getFile();
    public void refresh();

	public String getTitle();
	
	public boolean isModified();
	public boolean isBackedByFile();
	
	public void activate();
	
	public Buffer getActiveBuffer();

	public TabManager getTabManager();
	
	public <T> T getPresenter(Class<? extends T> clazz);
}
