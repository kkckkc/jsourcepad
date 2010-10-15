package kkckkc.jsourcepad.ui;

import com.google.common.collect.Maps;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.WindowManager;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;



public class ApplicationController implements WindowManager.Listener {
	private Map<Window, WindowPresenter> windows = Maps.newHashMap();

	@PostConstruct
	public void init() {
		System.setProperty("awt.useSystemAAFontSettings", "on");

		try {
			EventQueue.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					try {
						Object lnf = Application.get().getTheme().getLookAndFeel();
						if (lnf != null) {
                            if (lnf instanceof String) {
							    UIManager.setLookAndFeel((String) lnf);
                            } else {
                                UIManager.setLookAndFeel((LookAndFeel) lnf);
                            }
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		Application.get().getMessageBus().topic(WindowManager.Listener.class).subscribe(DispatchStrategy.SYNC, this);
	}

	
	@Override
	public void created(Window window) {
		WindowPresenter wv = window.getPresenter(WindowPresenter.class);
		
		windows.put(window, wv);
	}

	@Override
	public void destroyed(Window window) {
		WindowPresenter wv = windows.get(window);
		wv.dispose();
		
		windows.remove(window);
		
		if (windows.isEmpty()) {
			System.exit(0);
		}
	}
}
