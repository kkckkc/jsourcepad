package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;
import java.io.File;

public class ProjectOpenInNewWindowAction extends BaseAction {

	public ProjectOpenInNewWindowAction() {
        setActionStateRules(ActionStateRules.FILE_SELECTED, ActionStateRules.ONE_SELECTION);
	}

	@Override
	public void performAction(ActionEvent e) {
        Object[] tp = actionContext.get(ActionContextKeys.SELECTION);

        Window window = Application.get().getWindowManager().newWindow(null);
        window.getDocList().open((File) tp[0]);
	}

}
