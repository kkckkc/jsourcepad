package kkckkc.jsourcepad;

import com.google.common.base.Function;
import kkckkc.jsourcepad.http.RemoteControl;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.bundle.BundleManager;
import kkckkc.jsourcepad.model.settings.ProxySettings;
import kkckkc.jsourcepad.util.Config;
import kkckkc.jsourcepad.util.io.ErrorDialog;
import kkckkc.jsourcepad.util.io.SystemEnvironmentHelper;
import kkckkc.utils.PerformanceLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ServiceLoader;
import java.util.logging.Level;


@SuppressWarnings("restriction")
public class Bootstrap implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(Bootstrap.class);
    private String[] args;

    public static void main(String... args) throws IOException {
        logger.info("Initializing");

        RemoteControl remoteControl = new RemoteControl();
        if (remoteControl.isApplicationRunning()) {
            processArgsInRemoteApplication(remoteControl, args);
        } else {
            startApplication(args);
        }
	}

    private static void processArgsInRemoteApplication(RemoteControl remoteControl, String... args) {
        for (String arg : args) {
            remoteControl.open(arg);
        }
    }

    private static void startApplication(String... args) {
        PerformanceLogger.get().enter(Bootstrap.class.getName() + "#init");

        // Get and process startup listeners
        ServiceLoader<ApplicationLifecycleListener> loader = ServiceLoader.load(ApplicationLifecycleListener.class);
        for (ApplicationLifecycleListener listener : loader) {
            listener.applicationAboutToStart();
        }

        ThreadGroup tg = new ThreadGroup("Editor");

        // Setup global exception handler
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, final Throwable e) {
                java.util.logging.Logger logger = java.util.logging.Logger.getLogger("exceptions");
                logger.log(Level.WARNING, "Uncaught Exception", e);

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ErrorDialog errorDialog = Application.get().getErrorDialog();
                        errorDialog.show(e);
                    }
                });
            }
        });

        // Make sure shortdown is clean
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                WindowState.save();

                if (Config.getMode() == Config.MODE_DEVELOPMENT) {
                    PerformanceLogger.get().dump();
                }
            }
        });

        // Perform the actual startup in a new thread
        Thread mainThread = new Thread(tg, new Bootstrap(args));
        mainThread.start();

        // Process close down listeners
        for (ApplicationLifecycleListener listener : loader) {
            listener.applicationStarted();
        }
    }




    public Bootstrap(String... args) {
        this.args = args;
	}
	
	@Override
	public void run() {
        final StartupBenchmark startupBenchmark = new StartupBenchmark();

        final Runnable initializeApplicationContinuation = new Runnable() {
            @Override
            public void run() {
                boolean windowsRestored = WindowState.restore();

                try {
                    if (args == null || args.length == 0) {
                        // Create new window if none was restored
                        if (! windowsRestored) {
                            Window w = Application.get().getWindowManager().newWindow(null);
                            w.getDocList().create();
                        }
                    } else {
                        for (String s : args) {
                            File file = new File(s);
                            if (file.isDirectory()) {
                                Application.get().open(file);
                            }
                        }

                        for (String s : args) {
                            File file = new File(s);
                            if (file.isFile()) {
                                Application.get().open(file);
                            }
                        }
                    }

                    if (System.getProperty("startupScript") != null) {
                        try {
                            Application.get().getWindowManager().getWindows().iterator().next().getScriptEngine().
                                    eval(new FileReader(System.getProperty("startupScript")));
                        } catch (ScriptException se) {
                            throw new RuntimeException(se);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                final BundleManager bundleManager = Application.get().getBundleManager();

                KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                focusManager.addKeyEventDispatcher(new GlobalKeyEventDispatcher(bundleManager));

                // Initialize in background
                Application.get().getThreadPool().submit(new Runnable() {
                    @Override
                    public void run() {
                        SystemEnvironmentHelper.getSystemEnvironment();
                    }
                });
            }
        };

		EventQueue.invokeLater(new Runnable() {
			public void run() {
                Application.init();

                // Set proxy settings
                Application.get().getSettingsManager().get(ProxySettings.class).apply();

                if (isFirstStart()) {
                    @SuppressWarnings({"unchecked"})
                    Function<Runnable, Boolean> installer =
                            (Function<Runnable, Boolean>) Application.get().getBeanFactory().getBean("installer");
                    if (! installer.apply(initializeApplicationContinuation)) {
                        System.exit(1);
                    }
                } else {
                    initializeApplicationContinuation.run();
                }

                PerformanceLogger.get().exit();
                startupBenchmark.applicationInitComplete();
            }
        });

        startupBenchmark.applicationStartupComplete();

        startupBenchmark.execute();
    }

    public boolean isFirstStart() {
        return ! Config.getBundlesFolder().exists();
    }
}

