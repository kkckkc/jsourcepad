package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Finder;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class EditFindUseSelectionForReplaceAction extends BaseAction {
    private final Window window;

	public EditFindUseSelectionForReplaceAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

    @Override
    public void actionPerformed(ActionEvent e) {
		Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();

        String replacementText = "";
        if (buffer.getSelection() != null) {
            replacementText = buffer.getText(buffer.getSelection());
        }

        Finder finder = buffer.getFinder();
        Finder newFinder;
        if (finder == null) {
            newFinder = buffer.newFinder(buffer.getCompleteDocument(), "", new Finder.Options());
        } else {
            newFinder = buffer.newFinder(buffer.getCompleteDocument(), "", finder.getOptions());
        }
        newFinder.setReplacement(replacementText);
    }

}