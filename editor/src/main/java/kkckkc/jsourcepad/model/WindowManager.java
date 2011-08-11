package kkckkc.jsourcepad.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public Window getFocusedWindow();

    @NotNull public Window newWindow(@Nullable File file);
	public void closeWindow(@NotNull Window window);
	
	@NotNull public Collection<Window> getWindows();

    public void minimize(@NotNull Window window);
    public void maximize(@NotNull Window window);

}
