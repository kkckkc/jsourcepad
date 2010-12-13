package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class ProjectRenameAction extends BaseAction {

	private Window window;

	public ProjectRenameAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.FILE_OR_FOLDER_SELECTED, ActionStateRules.ONE_SELECTION);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        Object[] tp = actionContext.get(ActionContextKeys.SELECTION);

        String newName = JOptionPane.showInputDialog("New Name:");
        File fromFile = (File) tp[0];
        File toFile = new File(fromFile.getParentFile(), newName);
        fromFile.renameTo(toFile);

        window.getProject().refresh(fromFile);
        window.getProject().refresh(toFile);
        window.getProject().refresh(fromFile.getParentFile());
        window.getProject().refresh(toFile.getParentFile());
	}

}
