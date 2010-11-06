package kkckkc.jsourcepad.util;

import com.google.common.base.Predicate;

import java.io.File;

public interface FileMonitor {
	public void register(File file, Predicate<File> predicate);
	public void unregister(File file);

	public void addListener(Listener listener);

	public void requestRescan();
	
	public interface Listener {
		public void fileCreated(File file);
		public void fileRemoved(File file);
		public void fileUpdated(File file);
	}
}
