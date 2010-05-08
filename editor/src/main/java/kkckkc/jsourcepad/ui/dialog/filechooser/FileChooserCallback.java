package kkckkc.jsourcepad.ui.dialog.filechooser;

import java.io.File;

public interface FileChooserCallback {
	public void cancel();
	public void select(File selectedFiles);
}