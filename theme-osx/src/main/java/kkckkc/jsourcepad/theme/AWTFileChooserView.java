package kkckkc.jsourcepad.theme;

import kkckkc.jsourcepad.ui.dialog.filechooser.FileChooserCallback;
import kkckkc.jsourcepad.ui.dialog.filechooser.FileChooserView;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class AWTFileChooserView implements FileChooserView {

	@Override
	public void openDirectory(Component parent, File pwd,
			FileChooserCallback fileChooserCallback) {
		FileDialog fileDialog = new FileDialog((Frame) parent, "Open Directory", FileDialog.LOAD);
		fileDialog.setDirectory(canonicalize(pwd));
        System.setProperty("apple.awt.fileDialogForDirectories", "true");
		fileDialog.setVisible(true);
        System.setProperty("apple.awt.fileDialogForDirectories", "false");
		
		if (fileDialog.getFile() == null) {
			fileChooserCallback.cancel();
        } else if (new File(fileDialog.getDirectory(), fileDialog.getFile()).isFile()) {
            openDirectory(parent, pwd, fileChooserCallback);
		} else {
			fileChooserCallback.select(new File(fileDialog.getDirectory(), fileDialog.getFile()));
		}
	}

	@Override
	public void openFile(Component parent, File pwd,
			FileChooserCallback fileChooserCallback) {
		FileDialog fileDialog = new FileDialog((Frame) parent, "Open", FileDialog.LOAD);
		fileDialog.setDirectory(canonicalize(pwd));
		fileDialog.setVisible(true);
		
		if (fileDialog.getFile() == null) {
			fileChooserCallback.cancel();
		} else {
			fileChooserCallback.select(new File(fileDialog.getDirectory(), fileDialog.getFile()));
		}
	}

	@Override
	public void saveFile(Component parent, File pwd,
			FileChooserCallback fileChooserCallback, boolean confirmOverwrite) {
		FileDialog fileDialog = new FileDialog((Frame) parent, "Save", FileDialog.SAVE);
		fileDialog.setDirectory(canonicalize(pwd));
		fileDialog.setVisible(true);
		
		if (fileDialog.getFile() == null) {
			fileChooserCallback.cancel();
		} else {
			fileChooserCallback.select(new File(fileDialog.getDirectory(), fileDialog.getFile()));
		}	
	}

	private String canonicalize(File pwd) {
		try {
			return pwd.getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
