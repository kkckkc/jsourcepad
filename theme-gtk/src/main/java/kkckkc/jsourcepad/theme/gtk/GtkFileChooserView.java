package kkckkc.jsourcepad.theme.gtk;

import kkckkc.jsourcepad.ui.dialog.filechooser.FileChooserCallback;
import kkckkc.jsourcepad.ui.dialog.filechooser.FileChooserView;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GtkFileChooserView implements FileChooserView {

	@Override
	public void openDirectory(final Component parent, final File pwd, final FileChooserCallback fileChooserCallback) {
		try {
			SwingWorker<String, Void> open = new SwingWorker<String, Void>() {
				protected String doInBackground() throws Exception {
					ProcessBuilder pb = new ProcessBuilder("/usr/bin/zenity", "--file-selection", "--directory", "--title=Open Directory");
					pb.environment().put("WINDOWID", Long.toString(getX11WindowId(parent)));
					pb.directory(pwd);
					Process p = pb.start();
					synchronized (p) {
						p.wait();
					}
					String s = readOutput(p);

					if (p.exitValue() != 0) {
						fileChooserCallback.cancel();
					} else {
						fileChooserCallback.select(new File(s));
					}
					
					return s;
				}

			};
			open.execute();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	@Override
	public void openFile(final Component parent, final File pwd, final FileChooserCallback fileChooserCallback) {
		try {
			SwingWorker<String, Void> open = new SwingWorker<String, Void>() {
				protected String doInBackground() throws Exception {
					ProcessBuilder pb = new ProcessBuilder("/usr/bin/zenity", "--file-selection", "--title=Open");
					pb.environment().put("WINDOWID", Long.toString(getX11WindowId(parent)));
					pb.directory(pwd);
					Process p = pb.start();
					synchronized (p) {
						p.wait();
					}
					String s = readOutput(p);

					if (p.exitValue() != 0) {
						fileChooserCallback.cancel();
					} else {
						fileChooserCallback.select(new File(s));
					}
					
					return s;
				}

			};
			open.execute();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	@Override
	public void saveFile(final Component parent, final File pwd, final FileChooserCallback fileChooserCallback, boolean confirmOverwrite) {
		try {
			SwingWorker<String, Void> open = new SwingWorker<String, Void>() {
				protected String doInBackground() throws Exception {
					ProcessBuilder pb = new ProcessBuilder("/usr/bin/zenity", "--file-selection", "--save", "--title=Save As");
					pb.environment().put("WINDOWID", Long.toString(getX11WindowId(parent)));
					pb.directory(pwd);
					Process p = pb.start();
					synchronized (p) {
						p.wait();
					}
					String s = readOutput(p);

					if (p.exitValue() != 0) {
						fileChooserCallback.cancel();
					} else {
						fileChooserCallback.select(new File(s));
					}
					
					return s;
				}

			};
			open.execute();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	

	private long getX11WindowId(final Component parent)
			throws NoSuchFieldException, ClassNotFoundException,
			NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		Field peerField = Component.class.getDeclaredField("peer");
		peerField.setAccessible(true);
		Class<?> xWindowPeerClass = Class.forName("sun.awt.X11.XWindowPeer");
		Method getWindowMethod = xWindowPeerClass.getMethod("getWindow", new Class[0]);
		long windowId = (Long) getWindowMethod.invoke(peerField.get(parent), new Object[0]);
		return windowId;
	}	
	
	private String readOutput(Process p) throws IOException {
		return new BufferedReader(new InputStreamReader(p.getInputStream())).readLine();
	}
}
