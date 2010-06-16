package kkckkc.jsourcepad.model;

import java.io.File;
import java.util.List;

public interface Project {
	interface FileChangeListener {
		void created(File file);
		void refresh(File file);
	}

	public List<File> findFile(String query);
	public File getProjectDir();
	public void refresh(File file);

	public String getProjectRelativePath(String path);
	
	public List<File> getSelectedFiles();
	public void setSelectedFiles(List<File> paths);

}
