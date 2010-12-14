package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.WindowManager;
import kkckkc.jsourcepad.util.Config;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class BundlesBundleEditorShowAction extends BaseAction {

	@Override
	public void actionPerformed(ActionEvent e) {
        WindowManager wm = Application.get().getWindowManager();
		wm.newWindow(Config.getBundlesFolder());
	}

}
