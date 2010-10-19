package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class BundlesBundleEditorReloadAction extends BaseAction {

	@Override
	public void actionPerformed(ActionEvent e) {
        Application.get().getBundleManager().reload();
	}

}
