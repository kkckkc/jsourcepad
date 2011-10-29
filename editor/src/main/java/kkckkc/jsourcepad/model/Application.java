package kkckkc.jsourcepad.model;

import kkckkc.jsourcepad.Plugin;
import kkckkc.jsourcepad.PluginManager;
import kkckkc.jsourcepad.ScopeRoot;
import kkckkc.jsourcepad.model.bundle.BundleManager;
import kkckkc.jsourcepad.model.settings.GlobalSettingsManager;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.model.settings.StyleSettings;
import kkckkc.jsourcepad.theme.DefaultTheme;
import kkckkc.jsourcepad.theme.Theme;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import kkckkc.jsourcepad.util.Config;
import kkckkc.jsourcepad.util.Null;
import kkckkc.jsourcepad.util.command.CommandExecutor;
import kkckkc.jsourcepad.util.io.ErrorDialog;
import kkckkc.jsourcepad.util.messagebus.AbstractMessageBus;
import kkckkc.jsourcepad.util.messagebus.MessageBus;
import kkckkc.syntaxpane.parse.grammar.LanguageManager;
import kkckkc.syntaxpane.style.*;
import kkckkc.utils.io.FileUtils;
import kkckkc.utils.swing.ColorUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Application extends AbstractMessageBus implements MessageBus, ScopeRoot {
	private static Application application = new Application();

	private static Theme theme;

	BeanFactory beanFactory;

	private ExecutorService threadPool;
	private StyleScheme cachedStyleScheme;
    
    private GlobalSettingsManager settingsManager;
    private PersistentCacheManagerImpl persistentCacheManager;

    private BundleManager bundleManager;

    protected Application() {
        this.threadPool = Executors.newCachedThreadPool();
        this.settingsManager = new GlobalSettingsManager();
        this.persistentCacheManager = new PersistentCacheManagerImpl();
    }

	public static Application get() {
		return application;
	}

	public static synchronized void init() {
		BeanFactoryLoader loader = new BeanFactoryLoader();

        DefaultListableBeanFactory beanFactory = loader.load(BeanFactoryLoader.APPLICATION);

        // Bootstrapped bean
        beanFactory.registerSingleton("settingsManager", application.getSettingsManager());
        beanFactory.registerSingleton("persistentCacheManager", application.getPersistentCacheManager());
		beanFactory.registerSingleton("beanFactoryLoader", loader);
		beanFactory.registerSingleton("application", application);

        application.beanFactory = beanFactory;

        theme = initTheme();
        theme.activate();

        beanFactory.registerSingleton("theme", theme);

        // Load application controller
		beanFactory.getBean("applicationController");

        beanFactory.preInstantiateSingletons();
	}

    private static Theme initTheme() {
        for (Plugin p : PluginManager.getActivePlugins()) {
            if (! (p instanceof Theme)) continue;
            return (Theme) p;
        }

        return new DefaultTheme();
    }


    public PersistentCacheManager getPersistentCacheManager() {
        return persistentCacheManager;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

	public ExecutorService getThreadPool() {
	    return threadPool;
    }
	
	public WindowManager getWindowManager() {
		return beanFactory.getBean(WindowManager.class);
	}

	public LanguageManager getLanguageManager() {
        return beanFactory.getBean(LanguageManager.class);
    }

    public Browser getBrowser() {
        return beanFactory.getBean(Browser.class);
    }

    public LanguageSelectionRemembranceManager getLanguageSelectionRemembranceManager() {
        return beanFactory.getBean(LanguageSelectionRemembranceManager.class);
    }


	public StyleScheme getStyleScheme(StyleSettings styleSettings) {
		StyleParser styleParser = beanFactory.getBean(StyleParser.class);

        String location = new File(Config.getThemesFolder(), styleSettings.getThemeLocation()).toString();

		File source = new File(location);
		if (cachedStyleScheme != null && cachedStyleScheme.getSource().equals(source)) 
			return cachedStyleScheme;
		
		StyleScheme scheme = styleParser.parse(source);

        scheme = new DelegatingStyleScheme(scheme) {
            private boolean inited = false;
            private Map<ScopeSelector, TextStyle> styles;

            private synchronized void init() {
                BundleManager bundleManager = getBundleManager();
                Map<String, Map<ScopeSelector,Object>> preferences = bundleManager.getPreferences();

                if (preferences == null) {
                    this.inited = true;
                    return;
                }

                Map<ScopeSelector, Object> foregrounds = preferences.get("foreground");
                Map<ScopeSelector, Object> backgrounds = preferences.get("background");

                if (foregrounds == null || backgrounds == null) {
                    this.inited = true;
                    return;
                }

                Set<ScopeSelector> keys = new HashSet<ScopeSelector>(foregrounds.keySet());
                keys.addAll(backgrounds.keySet());

                Style defaultStyle = super.getTextStyle();

                styles = super.getStyles();
                for (ScopeSelector scopeSelector : keys) {
                    String foreground = (String) foregrounds.get(scopeSelector);
                    String background = (String) backgrounds.get(scopeSelector);

                    styles.put(scopeSelector,
                            new StyleBean(
                                    foreground == null ? defaultStyle.getColor() : ColorUtils.makeColor(foreground),
                                    background == null ? defaultStyle.getBackground() : ColorUtils.makeColor(background)));
                }

                this.inited = true;
            }

            @Override
            public Map<ScopeSelector, TextStyle> getStyles() {
                if (! inited) init();

                return styles;
            }
        };

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

	public BundleManager getBundleManager() {
        if (bundleManager == null) bundleManager = beanFactory.getBean(BundleManager.class);
		return bundleManager;
    }

	public Theme getTheme() {
		return theme;   
    }

	@Override
    public DefaultListableBeanFactory getBeanFactory() {
	    return (DefaultListableBeanFactory) this.beanFactory;
    }

    public ClipboardManager getClipboardManager() {
        return beanFactory.getBean(ClipboardManager.class);
    }

    public ErrorDialog getErrorDialog() {
        return beanFactory.getBean(ErrorDialog.class);
    }

    public Window open(File file) throws IOException {
        WindowManager wm = getWindowManager();

        file = file.getCanonicalFile();

        if (file.isDirectory()) {
            for (Window w : wm.getWindows()) {
                if (Null.Utils.isNull(w.getProject())) continue;
                if (file.equals(w.getProject().getProjectDir())) {
                    return w;
                }
            }

            Window window = wm.newWindow(file);
            window.getContainer().toFront();
            return window;
        } else {
            Window windowToUse = null;

            for (Window w : wm.getWindows()) {
                if (Null.Utils.isNull(w.getProject()) && windowToUse == null) {
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
            windowToUse.getContainer().toFront();

            return windowToUse;
        }
    }

    public CommandExecutor getCommandExecutor() {
        return beanFactory.getBean(CommandExecutor.class);
    }

}
