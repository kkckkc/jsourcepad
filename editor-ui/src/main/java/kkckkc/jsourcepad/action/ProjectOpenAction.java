package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;
import java.io.File;

public class ProjectOpenAction extends BaseAction {

	private Window window;

	public ProjectOpenAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.FILE_SELECTED, ActionStateRules.ONE_SELECTION);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        Object[] tp = actionContext.get(ActionContextKeys.SELECTION);

        window.getDocList().open((File) tp[0]);
	}

}
