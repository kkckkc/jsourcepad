package kkckkc.jsourcepad.ui;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.annotation.PostConstruct;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import kkckkc.jsourcepad.Presenter;
import kkckkc.jsourcepad.action.bundle.BundleAction;
import kkckkc.jsourcepad.action.bundle.BundleJMenuItem;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.DocList;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.Window.FocusedComponentType;
import kkckkc.jsourcepad.model.bundle.BundleManager;
import kkckkc.jsourcepad.util.action.MenuFactory;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;

import org.springframework.beans.factory.annotation.Autowired;


public class WindowPresenter implements Presenter<WindowView>, DocList.Listener {

	private JFrame frame;
	private Window window;
	private WindowView windowView;
	private BundleManager bundleManager;
	
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
		frame = window.getJFrame();
		frame.setTitle("JSourcePad");
		
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Application.get().getWindowManager().closeWindow(window);
			}
		});
		frame.setVisible(true);
		
		frame.addWindowFocusListener(new WindowFocusListener() {
			public void windowLostFocus(WindowEvent e) {
				window.topic(Window.FocusListener.class).post().focusLost(window);
			}
			
			public void windowGainedFocus(WindowEvent e) {
				window.topic(Window.FocusListener.class).post().focusGained(window);
			}
		});
		
		JMenuBar mb = windowView.getMenubar();
		
		MenuFactory menuFactory = new MenuFactory();

		mb.add(menuFactory.buildMenu("File", window.getActionManager().getActionGroup("file-menu"), null, false));
		mb.add(menuFactory.buildMenu("Edit", window.getActionManager().getActionGroup("edit-menu"), null, false));
		mb.add(menuFactory.buildMenu("View", window.getActionManager().getActionGroup("view-menu"), null, false));
		mb.add(menuFactory.buildMenu("Text", window.getActionManager().getActionGroup("text-menu"), null, false));
		mb.add(menuFactory.buildMenu("Navigation", window.getActionManager().getActionGroup("navigation-menu"), null, false));
		mb.add(menuFactory.buildMenu("Bundles", bundleManager.getBundleActionGroup(), new MenuFactory.ItemBuilder() {
			public JMenuItem build(Action action) {
				return new BundleJMenuItem((BundleAction) action);
			}
		}, true));

		
		window.topic(DocList.Listener.class).subscribe(DispatchStrategy.ASYNC_EVENT, this);
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

	public void bindFocus(JComponent view, final FocusedComponentType type) {
	    view.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
	            window.setFocusedComponent(type);
            }
		});
    }

	
}