package kkckkc.jsourcepad.model;

import java.awt.*;
import java.io.File;
import java.util.Collection;


public interface WindowManager {

    interface Listener {
		void created(Window window);
		void destroyed(Window window);
	}

    public Window getWindow(int id);

	public Window getWindow(Container jframe);
	
	public Window newWindow(File baseDir);

	public void closeWindow(Window window);
	
	public Collection<Window> getWindows();

	public Container getContainer(Window window);


    public void minimize(Window window);
    public void maximize(Window window);
}
