package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;
import java.io.File;


import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

public class ProjectFileOpenAction extends BaseAction {

	private Window window;

	public ProjectFileOpenAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.FILE_SELECTED);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        Object[] tp = actionContext.get(ActionContextKeys.SELECTION);
        if (! ( tp != null && tp.length > 0 && tp[0] instanceof File && ((File) tp[0]).isFile() )) return;

        window.getDocList().open((File) tp[0]);
	}

}
