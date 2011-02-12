package kkckkc.jsourcepad.action;

import com.google.common.io.Files;
import kkckkc.jsourcepad.model.Project;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class ProjectRemoveSelectedFilesAction extends BaseAction {

	private Window window;

	public ProjectRemoveSelectedFilesAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.FILE_OR_FOLDER_SELECTED);
	}

	@Override
	public void performAction(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog((Component) e.getSource(), "Really Delete");
        if (confirm == JOptionPane.OK_OPTION) {
            Project project = window.getProject();

            Object[] tp = actionContext.get(ActionContextKeys.SELECTION);
            for (Object o : tp) {
                try {
                    Files.deleteRecursively((File) o);

                    project.refresh(((File) o));
                    project.refresh(((File) o).getParentFile());
                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }
	}

}
