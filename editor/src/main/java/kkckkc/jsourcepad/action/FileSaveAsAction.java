package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;
import java.io.File;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.dialog.filechooser.FileChooserCallback;
import kkckkc.jsourcepad.ui.dialog.filechooser.FileSaveDialog;

public class FileSaveAsAction extends AbstractEditorAction {

	private FileSaveDialog fileSaveDialog;

	public FileSaveAsAction(Window window, FileSaveDialog fileSaveDialog) {
		super(window);
		
		subscribe(Event.DOC_SELECTION);

		this.fileSaveDialog = fileSaveDialog;
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

	@Override
	public boolean shouldBeEnabled(Object source) {
		Doc doc = window.getDocList().getActiveDoc();
		return doc != null;
	}

}
