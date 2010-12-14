package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class ProjectNewFolderAction extends BaseAction {

	private Window window;

	public ProjectNewFolderAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.FILE_OR_FOLDER_SELECTED, ActionStateRules.ONE_SELECTION);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        Object[] tp = actionContext.get(ActionContextKeys.SELECTION);
        File file = (File) tp[0];

        String name = JOptionPane.showInputDialog("File Name:");

        if (name == null || "".equals(name)) return;
        
        File folder;
        if (file.isDirectory()) {
            folder = new File(file, name);
        } else {
            folder = new File(file.getParentFile(), name);
        }

        folder.mkdir();

        window.getProject().refresh(folder.getParentFile());
	}


}
