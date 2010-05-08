package kkckkc.jsourcepad.action;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import kkckkc.jsourcepad.model.Project;
import kkckkc.jsourcepad.util.ui.PopupUtils;

public class ProjectRefreshAction extends AbstractAction {

	private Project project;

	public ProjectRefreshAction(Project project) {
		this.project = project;
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		JTree tree = (JTree) PopupUtils.getInvoker(e);
		Point p = PopupUtils.getRequestedLocation(e);
	    TreePath tp = tree.getPathForLocation((int) p.getX(), (int) p.getY());
	    if (tp != null) {
	    	project.refresh((File) tp.getLastPathComponent());
	    } else {
	    	project.refresh(null);
	    }
    }

}
