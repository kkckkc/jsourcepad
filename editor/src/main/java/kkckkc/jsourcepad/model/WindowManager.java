package kkckkc.jsourcepad.model;

import java.awt.Container;
import java.io.File;
import java.util.Collection;

import kkckkc.jsourcepad.ui.WindowPresenter;


public interface WindowManager {
	interface Listener {
		void created(Window window);
		void destroyed(Window window);
	}
	
	public Window getWindow(Container jframe);
	
	public Window newWindow(File baseDir);

	public void closeWindow(Window window);
	
	public Collection<Window> getWindows();

	public Container getContainer(Window window);
}
