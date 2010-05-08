package kkckkc.jsourcepad.ui.dialog.filechooser;

import java.awt.Component;
import java.io.File;

import kkckkc.jsourcepad.View;

public interface FileChooserView extends View {
	public void openFile(Component parent, File pwd, FileChooserCallback fileChooserCallback);
	public void saveFile(Component parent, File pwd, FileChooserCallback fileChooserCallback, boolean confirmOverwrite);
	public void openDirectory(Component parent, File pwd, FileChooserCallback fileChooserCallback);
}
