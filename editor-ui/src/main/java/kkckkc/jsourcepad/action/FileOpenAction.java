package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Doc;
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
	public void performAction(ActionEvent e) {
        File pwd = new File(".");
        Doc activeDoc = actionContext.get(ActionContextKeys.ACTIVE_DOC);
        if (activeDoc != null && activeDoc.isBackedByFile()) {
            pwd = activeDoc.getFile();
        }

		fileOpenDialog.show(pwd, new FileChooserCallback() {
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
