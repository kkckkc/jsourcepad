package kkckkc.jsourcepad.ui.dialog.filechooser;

import kkckkc.jsourcepad.Dialog;
import kkckkc.jsourcepad.model.Window;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

public class FileSaveDialog implements Dialog<FileChooserView> {
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
	
    public void show(File pwd, FileChooserCallback fileChooserCallback, boolean confirmOverwrite) {
    	view.saveFile(window.getContainer(), pwd, fileChooserCallback, confirmOverwrite);
    }

	@Override
    public void close() {
	    throw new RuntimeException("Not implemented");
    }

}
