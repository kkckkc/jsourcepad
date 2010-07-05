package kkckkc.jsourcepad.theme;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import kkckkc.jsourcepad.ui.dialog.filechooser.FileChooserCallback;
import kkckkc.jsourcepad.ui.dialog.filechooser.FileChooserView;

public class AWTFileChooserView implements FileChooserView {

	@Override
	public void openDirectory(Component parent, File pwd,
			FileChooserCallback fileChooserCallback) {
		FileDialog fileDialog = new FileDialog((Frame) parent, "Open Directory", FileDialog.LOAD);
		fileDialog.setDirectory(canonicalize(pwd));
		fileDialog.setVisible(true);
		
		if (fileDialog.getFile() == null) {
			fileChooserCallback.cancel();
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
			return pwd.getCanonicalPath().toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
