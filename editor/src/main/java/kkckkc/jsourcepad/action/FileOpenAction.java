package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;
import java.io.File;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.dialog.filechooser.FileChooserCallback;
import kkckkc.jsourcepad.ui.dialog.filechooser.FileOpenDialog;



public class FileOpenAction extends AbstractEditorAction {
	private FileOpenDialog fileOpenDialog;

	public FileOpenAction(Window window, FileOpenDialog fileOpenDialog) {
		super(window);
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
