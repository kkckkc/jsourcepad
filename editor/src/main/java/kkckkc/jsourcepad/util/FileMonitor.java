package kkckkc.jsourcepad.util;

import java.io.File;

import com.google.common.base.Predicate;

public interface FileMonitor {
	public void register(File file, Predicate<File> predicate);
	public void registerRecursively(File file, Predicate<File> predicate);
	
	public void addListener(Listener listener);

	public void requestRescan();
	
	public interface Listener {
		public void fileCreated(File file);
		public void fileRemoved(File file);
		public void fileUpdated(File file);
	}
}
