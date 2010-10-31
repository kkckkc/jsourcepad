package kkckkc.jsourcepad.model;

import kkckkc.jsourcepad.Plugin;
import kkckkc.jsourcepad.PluginManager;
import kkckkc.jsourcepad.ScopeRoot;
import kkckkc.jsourcepad.model.bundle.BundleManager;
import kkckkc.jsourcepad.theme.DefaultTheme;
import kkckkc.jsourcepad.theme.Theme;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import kkckkc.jsourcepad.util.Config;
import kkckkc.jsourcepad.util.io.ErrorDialog;
import kkckkc.jsourcepad.util.messagebus.AbstractMessageBus;
import kkckkc.jsourcepad.util.messagebus.MessageBus;
import kkckkc.syntaxpane.parse.grammar.LanguageManager;
import kkckkc.syntaxpane.style.StyleParser;
import kkckkc.syntaxpane.style.StyleScheme;
import kkckkc.utils.io.FileUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Application extends AbstractMessageBus implements MessageBus, ScopeRoot {
	private static Application application;

	private static Theme theme;

	BeanFactory beanFactory;

	private ExecutorService threadPool;
	private StyleScheme cachedStyleScheme;

	public static Application get() {
		if (application == null) {
			application = init();

            ((DefaultListableBeanFactory) application.beanFactory).preInstantiateSingletons();
		}
		return application;
	}
	
	private static synchronized Application init() {
		BeanFactoryLoader loader = new BeanFactoryLoader();
		DefaultListableBeanFactory beanFactory = loader.load(BeanFactoryLoader.APPLICATION);
		
		theme = initTheme();
		beanFactory.registerSingleton("theme", theme);
		beanFactory.registerSingleton("beanFactoryLoader", loader);

		return beanFactory.getBean(Application.class);
	}

	private static Theme initTheme() {
        for (Plugin p : PluginManager.getActivePlugins()) {
            if (! (p instanceof Theme)) continue;

            ((Theme) p).activate();
            return (Theme) p;
        }

        return new DefaultTheme();
	}

	protected Application() {
		this.threadPool = Executors.newCachedThreadPool();
	}

	@SuppressWarnings("restriction")
    public com.sun.net.httpserver.HttpServer getHttpServer() {
	    return beanFactory.getBean(com.sun.net.httpserver.HttpServer.class);
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
		return SettingsManager.GLOBAL;
	}
	
	public LanguageManager getLanguageManager() {
        return beanFactory.getBean(LanguageManager.class);
    }

	public StyleScheme getStyleScheme(StyleSettings styleSettings) {
		StyleParser styleParser = beanFactory.getBean(StyleParser.class);

        String location = new File(Config.getThemesFolder(), styleSettings.getThemeLocation()).toString();

		File source = new File(location);
		if (cachedStyleScheme != null && cachedStyleScheme.getSource().equals(source)) 
			return cachedStyleScheme;
		
		StyleScheme scheme = styleParser.parse(source);
		cachedStyleScheme = scheme;
		return scheme;
	}

    public String[] getStyleSchemes() {
        return Config.getThemesFolder().list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".tmTheme");
            }
        });
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

    public PersistenceManager getPersistenceManager() {
        return beanFactory.getBean(PersistenceManager.class);
    }

	@Override
    public BeanFactory getBeanFactory() {
	    return this.beanFactory;
    }

    public ClipboardManager getClipboardManager() {
        return beanFactory.getBean(ClipboardManager.class);
    }


    public Window open(File file) throws IOException {
        WindowManager wm = getWindowManager();

        file = file.getCanonicalFile();

        if (file.isDirectory()) {
            for (Window w : wm.getWindows()) {
                if (file.equals(w.getProject())) {
                    return w;
                }
            }

            return wm.newWindow(file);
        } else {
            Window windowToUse = null;

            for (Window w : wm.getWindows()) {
                if (w.getProject() == null && windowToUse == null) {
                    windowToUse = w;
                } else {
                    if (FileUtils.isAncestorOf(file, w.getProject().getProjectDir())) {
                        windowToUse = w;
                    }
                }
            }

            if (windowToUse == null) {
                windowToUse = wm.newWindow(null);
            }

            windowToUse.getDocList().open(file);

            return windowToUse;
        }
    }

    public ErrorDialog getErrorDialog() {
        return beanFactory.getBean(ErrorDialog.class);
    }
}
