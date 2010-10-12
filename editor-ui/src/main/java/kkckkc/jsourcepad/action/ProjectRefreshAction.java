package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Project;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;
import java.io.File;

public class ProjectRefreshAction extends BaseAction {

	private Project project;

	public ProjectRefreshAction(Project project) {
		this.project = project;
        setActionStateRules(ActionStateRules.HAS_PROJECT);
	}
	
    public void actionPerformed(ActionEvent e) {
        Object[] selection = actionContext.get(ActionContextKeys.SELECTION);
        boolean folderClicked =
                selection != null &&
                selection.length > 0 &&
                selection[0] instanceof File &&
                ((File) selection[0]).isDirectory();
	    if (folderClicked) {
	    	project.refresh((File) selection[0]);
	    } else {
	    	project.refresh(null);
	    }
    }

}
