package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Finder;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class EditFindUseSelectionForFindAction extends BaseAction {
    private final Window window;

	public EditFindUseSelectionForFindAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC, ActionStateRules.TEXT_SELECTED);
	}

    @Override
    public void actionPerformed(ActionEvent e) {
		Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();

        Finder finder = buffer.getFinder();
        if (finder == null) {
            buffer.newFinder(buffer.getCompleteDocument(),
                    buffer.getText(buffer.getSelection()),
                    new Finder.Options());
        } else {
            Finder newFinder = buffer.newFinder(buffer.getCompleteDocument(),
                    buffer.getText(buffer.getSelection()),
                    finder.getOptions());
            newFinder.setReplacement(finder.getReplacement());
        }
    }

}