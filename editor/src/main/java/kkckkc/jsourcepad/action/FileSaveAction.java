package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.Window;

public class FileSaveAction extends AbstractEditorAction {

	private FileSaveAsAction fileSaveAsAction;

	public FileSaveAction(Window window, FileSaveAsAction fileSaveAsAction) {
		super(window);
		
		subscribe(Event.DOC_SELECTION, Event.DOC_MODIFICATION);
		
		this.fileSaveAsAction = fileSaveAsAction;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (! window.getDocList().getActiveDoc().isBackedByFile()) {
			fileSaveAsAction.actionPerformed(e);
		} else {
			window.getDocList().getActiveDoc().save();
		}
	}

	@Override
	public boolean shouldBeEnabled(Object source) {
		Doc doc = window.getDocList().getActiveDoc();
		return doc != null && (! doc.isBackedByFile() || doc.isModified());
	}

}
