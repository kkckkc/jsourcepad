package kkckkc.jsourcepad.model;

import com.google.common.collect.Maps;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import kkckkc.utils.swing.ComponentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;


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
	public Window newWindow(File file) {
        Properties props = new Properties();
        props.put("projectDir", file == null ? "" : file.toString());

        // TODO: Clean this method
        if (file != null && file.isDirectory()) {
            DefaultListableBeanFactory container =
                beanFactoryLoader.load(BeanFactoryLoader.WINDOW, app, file, props);

            Window window = container.getBean("window", Window.class);
            ((WindowImpl) window).setId(++lastId);

            Container frame = window.getContainer();
            container.registerSingleton("frame", frame);

            openWindows.put(frame, window);
            app.getMessageBus().topic(Listener.class).post().created(window);
            return window;
        } else {
            DefaultListableBeanFactory container =
                beanFactoryLoader.load(BeanFactoryLoader.WINDOW, app, file, props);

            Window window = container.getBean("window", Window.class);
            ((WindowImpl) window).setId(++lastId);

            Container frame = window.getContainer();
            container.registerSingleton("frame", frame);

            openWindows.put(frame, window);
            app.getMessageBus().topic(Listener.class).post().created(window);

            if (file != null) {
                window.getDocList().open(file);
            }

            return window;
        }
	}

	@Override
	public void closeWindow(Window window) {
		openWindows.remove(window.getContainer());
		app.getMessageBus().topic(Listener.class).post().destroyed(window);
	}

	@Override
    public Collection<Window> getWindows() {
	    return openWindows.values();
    }


    @Override
    public void minimize(Window window) {
        JFrame c = (JFrame) window.getContainer();
        c.setState(JFrame.ICONIFIED);
    }

    @Override
    public void maximize(Window window) {
        JFrame c = (JFrame) window.getContainer();
        if (c.getState() == JFrame.MAXIMIZED_BOTH) {
            c.setState(JFrame.NORMAL);
        } else {
            c.setState(JFrame.MAXIMIZED_BOTH);
        }
    }
}
