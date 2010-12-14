package kkckkc.jsourcepad.ui.dialog.filechooser;

import kkckkc.jsourcepad.View;

import java.awt.*;
import java.io.File;

public interface FileChooserView extends View {
	public void openFile(Component parent, File pwd, FileChooserCallback fileChooserCallback);
	public void saveFile(Component parent, File pwd, FileChooserCallback fileChooserCallback, boolean confirmOverwrite);
	public void openDirectory(Component parent, File pwd, FileChooserCallback fileChooserCallback);
}
