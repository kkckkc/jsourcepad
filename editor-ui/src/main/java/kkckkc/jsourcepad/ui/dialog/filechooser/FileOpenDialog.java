package kkckkc.jsourcepad.ui.dialog.filechooser;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;

import kkckkc.jsourcepad.Dialog;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.WindowManager;

public class FileOpenDialog implements Dialog<FileChooserView> {
	private FileChooserView view;
	private Window window;

	@Autowired
	public void setWindow(Window window) {
	    this.window = window;
    }
	
	@Autowired
    public void setView(FileChooserView view) {
		this.view = view;
	}
	
    public void show(File pwd, FileChooserCallback fileChooserCallback) {
    	view.openFile(window.getContainer(), pwd, fileChooserCallback);
    }

	@Override
    public void close() {
	    throw new RuntimeException("Not implemented");
    }

}