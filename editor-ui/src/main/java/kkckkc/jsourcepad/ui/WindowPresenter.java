package kkckkc.jsourcepad.ui;

import com.google.common.io.Resources;
import kkckkc.jsourcepad.Presenter;
import kkckkc.jsourcepad.action.bundle.BundleAction;
import kkckkc.jsourcepad.action.bundle.BundleJMenuItem;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.DocList;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.bundle.BundleManager;
import kkckkc.jsourcepad.model.bundle.BundleMenuProvider;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.model.settings.WindowSettings;
import kkckkc.jsourcepad.util.action.ActionGroup;
import kkckkc.jsourcepad.util.action.MenuFactory;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Arrays;


public class WindowPresenter implements Presenter<WindowView>, DocList.Listener {

	private Window window;
	private WindowView windowView;
	private BundleManager bundleManager;
	private JFrame frame;
	
	@Autowired
	public void setWindow(Window window) {
	    this.window = window;
    }
	
	@Autowired
	public void setBundleManager(BundleManager bundleManager) {
	    this.bundleManager = bundleManager;
    }
	
	@Autowired
    public void setView(WindowView view) {
	    this.windowView = view;
    }


	@PostConstruct
    public void init() throws Exception {
		frame = window.getContainer();

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        
        frame.setIconImages(Arrays.asList(
                toolkit.createImage(Resources.toByteArray(getClass().getResource("/icons/icon_16x16x32.png"))),
                toolkit.createImage(Resources.toByteArray(getClass().getResource("/icons/icon_32x32x32.png"))),
                toolkit.createImage(Resources.toByteArray(getClass().getResource("/icons/icon_48x48x32.png"))),
                toolkit.createImage(Resources.toByteArray(getClass().getResource("/icons/icon_128x128x32.png"))),
                toolkit.createImage(Resources.toByteArray(getClass().getResource("/icons/icon_256x256x32.png")))
                ));
        windowView.setJFrame(frame);
        
		frame.setTitle("JSourcePad");
		
		frame.setLocationRelativeTo(null);
        frame.setLocationByPlatform(true);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Application.get().getWindowManager().closeWindow(window);
			}
		});

		frame.addWindowFocusListener(new WindowFocusListener() {
			public void windowLostFocus(WindowEvent e) {
				window.topic(Window.FocusListener.class).post().focusLost(window);
			}
			
			public void windowGainedFocus(WindowEvent e) {
				window.topic(Window.FocusListener.class).post().focusGained(window);
			}
		});
		
		final JMenuBar mb = windowView.getMenubar();

        final JMenu fileMenu = new JMenu("File");
        final JMenu editMenu = new JMenu("Edit");
        final JMenu viewMenu = new JMenu("View");
        final JMenu textMenu = new JMenu("Text");
        final JMenu navigationMenu = new JMenu("Navigation");
        final JMenu bundleMenu = new JMenu("Bundles");
        final JMenu windowMenu = new JMenu("Window");

        mb.add(fileMenu);
        mb.add(editMenu);
        mb.add(viewMenu);
        mb.add(textMenu);
        mb.add(navigationMenu);
        mb.add(bundleMenu);
        mb.add(windowMenu);

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                MenuFactory menuFactory = new MenuFactory();

                menuFactory.buildMenu(fileMenu, window.getActionManager().getActionGroup("file-menu"), null, false);
                menuFactory.buildMenu(editMenu, window.getActionManager().getActionGroup("edit-menu"), null, false);
                menuFactory.buildMenu(viewMenu, window.getActionManager().getActionGroup("view-menu"), null, false);
                menuFactory.buildMenu(textMenu, window.getActionManager().getActionGroup("text-menu"), null, false);
                menuFactory.buildMenu(navigationMenu, window.getActionManager().getActionGroup("navigation-menu"), null, false);

                ActionGroup bundlesAg = window.getActionManager().getActionGroup("bundles-menu");
                for (Action o : BundleMenuProvider.getBundleActionGroup().getItems()) {
                    bundlesAg.add(o);
                }
                menuFactory.buildMenu(bundleMenu, bundlesAg, new MenuFactory.ItemBuilder() {
                    public JMenuItem build(Action action) {
                        if (action instanceof BundleAction) {
                            return new BundleJMenuItem((BundleAction) action);
                        } else {
                            return new JMenuItem(action);
                        }
                    }
                }, true);
                menuFactory.buildMenu(windowMenu, window.getActionManager().getActionGroup("window-menu"), null, false);
            }
        });

        window.topic(DocList.Listener.class).subscribe(DispatchStrategy.ASYNC_EVENT, WindowPresenter.this);
        frame.setVisible(true);

        final Application app = Application.get();
        app.getSettingsManager().subscribe(WindowSettings.class, new SettingsManager.Listener<WindowSettings>() {
            public void settingUpdated(WindowSettings settings) {
                windowView.setShowProjectDrawer(settings.isShowProjectDrawer());
            }
        }, false, app, window);
    }
	
	
	
	public void dispose() {
		this.frame.dispose();
	}

	public void closed(int index, Doc doc) {
		this.frame.setTitle("JSourcePad");
	}

	public void created(Doc doc) {
	}

	public void selected(int index, Doc doc) {
		this.frame.setTitle("JSourcePad - " + doc.getTitle());
	}

}