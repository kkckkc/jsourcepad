package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.dialog.newfile.NewFileDialog;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;
import java.io.File;

public class ProjectNewFileAction extends BaseAction {

	private Window window;

	public ProjectNewFileAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.FILE_OR_FOLDER_SELECTED, ActionStateRules.ONE_SELECTION);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        Object[] tp = actionContext.get(ActionContextKeys.SELECTION);
        File file = (File) tp[0];

        NewFileDialog newFileDialog = window.getPresenter(NewFileDialog.class);
        if (file.isDirectory()) {
            newFileDialog.show(file);
        } else {
            newFileDialog.show(file.getParentFile());
        }
/*
        String name = JOptionPane.showInputDialog("File Name:");


        File f;
        if (file.isDirectory()) {
            f = new File(file, name);
        } else {
            f = new File(file.getParentFile(), name);
        }

        try {
            f.createNewFile();
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
        
        window.getProject().refresh(f.getParentFile());
        window.getDocList().open(f);
        */
	}

}
