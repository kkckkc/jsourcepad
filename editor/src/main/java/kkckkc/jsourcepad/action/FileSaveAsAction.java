package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;
import java.io.File;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.dialog.filechooser.FileChooserCallback;
import kkckkc.jsourcepad.ui.dialog.filechooser.FileSaveDialog;
import kkckkc.jsourcepad.util.action.ActionContext;
import kkckkc.jsourcepad.util.action.BaseAction;

public class FileSaveAsAction extends BaseAction {

	private FileSaveDialog fileSaveDialog;
    private final Window window;

	public FileSaveAsAction(Window window, FileSaveDialog fileSaveDialog) {
		this.window = window;
		this.fileSaveDialog = fileSaveDialog;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final Doc doc = window.getDocList().getActiveDoc();
		fileSaveDialog.show(new File("."), new FileChooserCallback() {
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
