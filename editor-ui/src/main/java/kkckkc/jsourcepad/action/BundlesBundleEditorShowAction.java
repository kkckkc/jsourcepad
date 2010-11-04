package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.WindowManager;
import kkckkc.jsourcepad.ui.dialog.filechooser.DirectoryOpenDialog;
import kkckkc.jsourcepad.util.Config;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class BundlesBundleEditorShowAction extends BaseAction {

    private Window window;
    private DirectoryOpenDialog directoryOpenDialog;

    public BundlesBundleEditorShowAction(Window window, DirectoryOpenDialog directoryOpenDialog) {
		this.window = window;
		this.directoryOpenDialog = directoryOpenDialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        WindowManager wm = Application.get().getWindowManager();
		wm.newWindow(Config.getBundlesFolder());
	}

}
