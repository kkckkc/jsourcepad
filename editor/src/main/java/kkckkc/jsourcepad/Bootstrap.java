package kkckkc.jsourcepad;

import com.sun.net.httpserver.HttpServer;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.bundle.BundleManager;
import kkckkc.jsourcepad.ui.ApplicationController;
import kkckkc.jsourcepad.util.PerformanceLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;




@SuppressWarnings("restriction")
public class Bootstrap implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(Bootstrap.class);
	
	public static void main(String... args) throws IOException {
        logger.info("Initializing");

        PerformanceLogger.get().enter(Bootstrap.class.getName() + "#init");

		ThreadGroup tg = new ThreadGroup("Editor") {
			public void uncaughtException(Thread t, Throwable e) {
				super.uncaughtException(t, e);
			}
		};
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				PerformanceLogger.get().dump();
			}
		});

		Bootstrap b = new Bootstrap();
		
		Thread mainThread = new Thread(tg, b);
		mainThread.start();
	}

	
	public Bootstrap() {
		ApplicationController c = Application.get().getApplicationController();
	}
	
	@Override
	public void run() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				// Create new window
				try {
	                Window w = Application.get().getWindowManager().newWindow(new File(".").getCanonicalFile());
	                if (System.getProperty("startupScript") != null) {
		                try {
		                    w.getScriptEngine().eval(new FileReader(System.getProperty("startupScript")));
	                    } catch (ScriptException e1) {
		                    e1.printStackTrace();
	                    }
	                }
                } catch (IOException e) {
	                throw new RuntimeException(e);
                }

                final BundleManager bundleManager = Application.get().getBundleManager();
                
        		KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        		focusManager.addKeyEventDispatcher(new GlobalKeyEventDispatcher(bundleManager));

                PerformanceLogger.get().exit();
            }
        });

        HttpServer server = Application.get().getHttpServer();
	}
}
