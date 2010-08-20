package kkckkc.jsourcepad.model;

import java.awt.Container;
import java.io.File;
import java.util.Collection;
import java.util.Map;

import javax.swing.JFrame;

import kkckkc.jsourcepad.theme.Theme;
import kkckkc.jsourcepad.ui.WindowPresenter;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import kkckkc.jsourcepad.util.ui.ComponentUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.google.common.collect.Maps;



public class WindowManagerImpl implements WindowManager {
	private Map<Container, Window> openWindows = Maps.newHashMap();
	
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
	public Window getWindow(Container c) {
		return openWindows.get(ComponentUtils.getToplevelAncestor(c));
	}

	@Override
	public Window newWindow(File projectDir) {

		DefaultListableBeanFactory container = 
			beanFactoryLoader.load(BeanFactoryLoader.WINDOW, app);
		
		container.registerSingleton("projectDir", new ProjectRoot(projectDir));
		
		Window window = container.getBean("window", Window.class);
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
}
