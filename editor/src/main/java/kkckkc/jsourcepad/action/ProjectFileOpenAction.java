package kkckkc.jsourcepad.action;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.jsourcepad.util.ui.PopupUtils;

public class ProjectFileOpenAction extends BaseAction {

	private Window window;

	public ProjectFileOpenAction(Window window) {
		this.window = window;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JTree tree = (JTree) PopupUtils.getInvoker(e);
		Point p = PopupUtils.getRequestedLocation(e);
		TreePath tp = tree.getPathForLocation((int) p.getX(), (int) p.getY());
		if (tp != null) {
			window.getDocList().open((File) tp.getLastPathComponent());
		}
	}

	@Override
	public boolean shouldBeEnabled(Object source) {
		JTree tree = (JTree) PopupUtils.getInvoker(source);
		Point p = PopupUtils.getRequestedLocation(source);
		TreePath tp = tree.getPathForLocation((int) p.getX(), (int) p.getY());
		return tp != null && ((File) tp.getLastPathComponent()).isFile();
	}
}
