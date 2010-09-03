package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.DocPresenter;
import kkckkc.jsourcepad.ui.dialog.find.FindDialog;
import kkckkc.jsourcepad.ui.dialog.find.FindDialogView;

public class EditFindAction extends AbstractEditorAction {

	public EditFindAction(Window window) {
		super(window);
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		FindDialog dialog = window.getPresenter(FindDialog.class);
		dialog.show();
    }

}
