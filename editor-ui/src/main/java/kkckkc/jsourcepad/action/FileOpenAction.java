package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.dialog.filechooser.FileChooserCallback;
import kkckkc.jsourcepad.ui.dialog.filechooser.FileOpenDialog;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;
import java.io.File;



public class FileOpenAction extends BaseAction {
	private FileOpenDialog fileOpenDialog;
    private final Window window;

	public FileOpenAction(Window window, FileOpenDialog fileOpenDialog) {
		this.window = window;
		this.fileOpenDialog = fileOpenDialog;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		fileOpenDialog.show(new File("."), new FileChooserCallback() {
			@Override
			public void cancel() {
			}

			@Override
			public void select(File selectedFile) {
				window.getDocList().open(selectedFile);
			}
		});
	}
}
