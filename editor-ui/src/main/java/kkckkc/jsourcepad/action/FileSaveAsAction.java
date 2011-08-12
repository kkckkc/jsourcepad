package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.ui.dialog.filechooser.FileChooserCallback;
import kkckkc.jsourcepad.ui.dialog.filechooser.FileSaveDialog;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;
import java.io.File;

public class FileSaveAsAction extends BaseAction {

	private FileSaveDialog fileSaveDialog;

	public FileSaveAsAction(FileSaveDialog fileSaveDialog) {
		this.fileSaveDialog = fileSaveDialog;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
	public void performAction(ActionEvent e) {
		final Doc doc = actionContext.get(ActionContextKeys.ACTIVE_DOC);

        File pwd = new File(".");
        if (doc.isBackedByFile()) {
            pwd = doc.getFile();
        }

		fileSaveDialog.show(pwd, new FileChooserCallback() {
			@Override
			public void cancel() {
			}

			@Override
			public void select(File selectedFiles) {
				doc.saveAs(selectedFiles);
			}
			
		}, true);
	}

}
