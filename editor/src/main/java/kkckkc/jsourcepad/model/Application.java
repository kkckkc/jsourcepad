package kkckkc.jsourcepad.model;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kkckkc.jsourcepad.ScopeRoot;
import kkckkc.jsourcepad.model.bundle.BundleManager;
import kkckkc.jsourcepad.theme.DefaultTheme;
import kkckkc.jsourcepad.theme.Theme;
import kkckkc.jsourcepad.ui.ApplicationController;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import kkckkc.jsourcepad.util.messagebus.AbstractMessageBus;
import kkckkc.jsourcepad.util.messagebus.MessageBus;
import kkckkc.syntaxpane.parse.grammar.LanguageManager;
import kkckkc.syntaxpane.style.StyleParser;
import kkckkc.syntaxpane.style.StyleScheme;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;



public class Application extends AbstractMessageBus implements MessageBus, ScopeRoot {
	private static Application application;

	private static Theme theme;

	BeanFactory beanFactory;

	private ExecutorService threadPool;
	private StyleScheme cachedStyleScheme;

	public synchronized static Application get() {
		if (application == null) {
			application = init();
		}
		return application;
	}
	
	private static Application init() {
		BeanFactoryLoader loader = new BeanFactoryLoader();
		DefaultListableBeanFactory beanFactory = loader.load(BeanFactoryLoader.APPLICATION);
		
		theme = initTheme();
		beanFactory.registerSingleton("theme", theme);
		beanFactory.registerSingleton("beanFactoryLoader", loader);
		
		Application a = beanFactory.getBean(Application.class);
		
		return a;
	}

	private static Theme initTheme() {
		String theme = System.getProperty("theme");
		if (theme == null) return new DefaultTheme();
		else {
			try {
				Class<?> c = Class.forName(theme);
				return (Theme) c.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	protected Application() {
		this.threadPool = Executors.newCachedThreadPool();
	}

	@SuppressWarnings("restriction")
    public com.sun.net.httpserver.HttpServer getHttpServer() {
	    return beanFactory.getBean(com.sun.net.httpserver.HttpServer.class);
    }
	
	public ApplicationController getApplicationController() {
		return beanFactory.getBean(ApplicationController.class);
	}

	public ExecutorService getThreadPool() {
	    return threadPool;
    }
	
	public WindowManager getWindowManager() {
		return beanFactory.getBean(WindowManager.class);
	}

	public MessageBus getMessageBus() {
		return beanFactory.getBean(MessageBus.class);
	}

	public SettingsManager getSettingsManager() {
		return beanFactory.getBean(SettingsManager.class);
	}
	
	public LanguageManager getLanguageManager() {
		return beanFactory.getBean(LanguageManager.class);
	}

	public StyleScheme getStyleScheme(StyleSettings styleSettings) {
		StyleParser styleParser = beanFactory.getBean(StyleParser.class);
		
		File source = new File(styleSettings.getThemeLocation());
		if (cachedStyleScheme != null && cachedStyleScheme.getSource().equals(source)) 
			return cachedStyleScheme;
		
		StyleScheme scheme = styleParser.parse(source);
		cachedStyleScheme = scheme;
		return scheme;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	public BundleManager getBundleManager() {
		return beanFactory.getBean(BundleManager.class);   
    }

	public Theme getTheme() {
		return theme;   
    }

	@Override
    public BeanFactory getBeanFactory() {
	    return this.beanFactory;
    }

    public ClipboardManager getClipboardManager() {
        return beanFactory.getBean(ClipboardManager.class);
    }
}	
