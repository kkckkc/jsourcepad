package kkckkc.jsourcepad.ui;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.swing.UIManager;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.WindowManager;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;



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
						String lnf = Application.get().getTheme().getLookAndFeel();
						if (lnf != null) {
							UIManager.setLookAndFeel(lnf);
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
