package kkckkc.jsourcepad.model;

import java.io.File;

public interface DocList {
    public interface Listener {
		public void created(Doc doc);
		public void selected(int index, Doc doc);
		public void closed(int index, Doc doc);
	}
	
	public Doc open(File file);
	public Doc create();
	
	public Iterable<Doc> getDocs();
	public int getDocCount();
	
	public Window getWindow();
	
	public int getActive();
	public void setActive(int selectedIndex);
    public void setActive(Doc doc);
	public Doc getActiveDoc();
	
	public void close(Doc doc);
}
