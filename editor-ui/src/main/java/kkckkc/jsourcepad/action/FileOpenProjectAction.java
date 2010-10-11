package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.WindowManager;
import kkckkc.jsourcepad.ui.dialog.filechooser.DirectoryOpenDialog;
import kkckkc.jsourcepad.ui.dialog.filechooser.FileChooserCallback;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;
import java.io.File;



public class FileOpenProjectAction extends BaseAction {

    private Window window;
    private DirectoryOpenDialog directoryOpenDialog;

    public FileOpenProjectAction(Window window, DirectoryOpenDialog directoryOpenDialog) {
		this.window = window;
		this.directoryOpenDialog = directoryOpenDialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		directoryOpenDialog.show(new File("."), new FileChooserCallback() {
			@Override
			public void cancel() {
			}

			@Override
			public void select(File selectedFile) {
                WindowManager wm = Application.get().getWindowManager();
		        Window w = wm.newWindow(selectedFile);
			}
		});
	}

}