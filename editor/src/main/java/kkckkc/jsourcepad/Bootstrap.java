package kkckkc.jsourcepad;

import java.awt.EventQueue;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

import javax.script.ScriptException;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.bundle.BundleItemSupplier;
import kkckkc.jsourcepad.model.bundle.BundleManager;
import kkckkc.jsourcepad.ui.ApplicationController;
import kkckkc.jsourcepad.util.PerformanceLogger;
import kkckkc.jsourcepad.util.action.ActionGroup;
import kkckkc.jsourcepad.util.action.MenuFactory;
import kkckkc.jsourcepad.util.ui.PopupUtils;
import kkckkc.syntaxpane.model.Scope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpServer;




@SuppressWarnings("restriction")
public class Bootstrap implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(Bootstrap.class);
	
	public static void main(String... args) throws IOException {
        logger.info("Initializing");
        
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
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
        		BundleManager bm = Application.get().getBundleManager();
        		bm.getBundles();
	            return null;
            }
		};
		worker.execute();
		
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
        		focusManager.addKeyEventDispatcher(new KeyEventDispatcher() {
                    public boolean dispatchKeyEvent(final KeyEvent e) {
        				if (e.getID() == KeyEvent.KEY_RELEASED)
        					return false;
        				
        				if (e.getKeyCode() == KeyEvent.VK_CONTROL || 
        					e.getKeyCode() == KeyEvent.VK_SHIFT ||
        					e.getKeyCode() == KeyEvent.VK_META || 
        					e.getKeyCode() == KeyEvent.VK_META  || 
        					e.getKeyCode() == KeyEvent.VK_ALT)
        					return false;
        				
        				if (e.getID() == KeyEvent.KEY_TYPED) return false;
        				
        				// Ignore letters and digits without modifiers
        				if ((Character.isLetter(e.getKeyChar()) || Character.isDigit(e.getKeyChar())) && 
        						e.getModifiers() <= 1) 
        					return false;

                    	Window window = Application.get().getWindowManager().getWindow((JComponent) e.getComponent());
                    	if (window == null) return false;
                    	if (window.getDocList().getActiveDoc() == null) return false;

        				Scope scope = window.getDocList().getActiveDoc().getActiveBuffer().getInsertionPoint().getScope();
        				
        				Collection<BundleItemSupplier> items = bundleManager.getItemsForShortcut(e, scope);
        				if (! items.isEmpty()) {
        					final ActionGroup tempActionGroup = new ActionGroup();
        					
	        				for (BundleItemSupplier r : items) {
	        					tempActionGroup.add(r.getAction());
	        				}
	        				
	        				if (tempActionGroup.size() > 1) {
		        				
		        				EventQueue.invokeLater(new Runnable() {
	                                public void run() {
	        	        				JPopupMenu jpm = new MenuFactory().buildPopup(tempActionGroup, null);
	        	        				Point point = MouseInfo.getPointerInfo().getLocation();
	        	        				
	        	        				Point componentPosition = e.getComponent().getLocationOnScreen();
	        	        				
	        	        				point.translate(- (int) componentPosition.getX(), - (int) componentPosition.getY());
	        	        				
	        	        				PopupUtils.show(jpm, point, e.getComponent());
	                                }
		        				});
		        				
	        				} else {

	        					tempActionGroup.get(0).actionPerformed(
	        							new ActionEvent(e.getComponent(), 1, null));
	        					
	        				}
	        				
	        				e.consume();
	        				return true;
        				}
        				
        				return false;
        			}
        		});
            }
		});

        HttpServer server = Application.get().getHttpServer();
	}
}
