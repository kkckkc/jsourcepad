package kkckkc.jsourcepad.model;

import com.google.common.collect.Maps;
import kkckkc.jsourcepad.ui.WindowPresenter;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import kkckkc.utils.swing.ComponentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Collection;
import java.util.Map;



public class WindowManagerImpl implements WindowManager {
	private Map<Container, Window> openWindows = Maps.newHashMap();
	private int lastId = 0;

	// Collaborators
	private Application app;
	private BeanFactoryLoader beanFactoryLoader;

	@Autowired
	public void setApp(Application app) {
	    this.app = app;
    }

	@Autowired
	public void setBeanFactoryLoader(BeanFactoryLoader beanFactoryLoader) {
	    this.beanFactoryLoader = beanFactoryLoader;
    }


    @Override
    public Window getWindow(int id) {
        for (Window window : openWindows.values()) {
            if (window.getId() == id) return window;
        }
        return null;
    }

    @Override
	public Window getWindow(Container c) {
		return openWindows.get(ComponentUtils.getToplevelAncestor(c));
	}

	@Override
	public Window newWindow(File projectDir) {
		DefaultListableBeanFactory container =
			beanFactoryLoader.load(BeanFactoryLoader.WINDOW, app);

		container.registerSingleton("projectDir", new ProjectRoot(projectDir));

		Window window = container.getBean("window", Window.class);
        window.setId(++lastId);

		Container frame = getContainer(window);

		container.registerSingleton("frame", frame);

		openWindows.put(frame, window);
		app.getMessageBus().topic(Listener.class).post().created(window);
		return window;
	}

	@Override
	public void closeWindow(Window window) {
		openWindows.remove(getContainer(window));
		app.getMessageBus().topic(Listener.class).post().destroyed(window);
	}

	@Override
    public Collection<Window> getWindows() {
	    return openWindows.values();
    }
	
	public Container getContainer(Window window) {
		return window.getPresenter(WindowPresenter.class).getContainer();
	}

    @Override
    public void minimize(Window window) {
        JFrame c = (JFrame) getContainer(window);
        c.setState(JFrame.ICONIFIED);
    }

    @Override
    public void maximize(Window window) {
        JFrame c = (JFrame) getContainer(window);
        if (c.getState() == JFrame.MAXIMIZED_BOTH) {
            c.setState(JFrame.NORMAL);
        } else {
            c.setState(JFrame.MAXIMIZED_BOTH);
        }
    }
}
