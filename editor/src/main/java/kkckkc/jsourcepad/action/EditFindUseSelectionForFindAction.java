package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.Finder;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class EditFindUseSelectionForFindAction extends BaseAction {
	public EditFindUseSelectionForFindAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC, ActionStateRules.TEXT_SELECTED);
	}

    @Override
    public void actionPerformed(ActionEvent e) {
        Doc d = actionContext.get(ActionContextKeys.ACTIVE_DOC);
		Buffer buffer = d.getActiveBuffer();

        Finder finder = buffer.getFinder();
        if (finder == null) {
            buffer.newFinder(buffer.getCompleteDocument(),
                    buffer.getSelection().getText(),
                    new Finder.Options());
        } else {
            Finder newFinder = buffer.newFinder(buffer.getCompleteDocument(),
                    buffer.getSelection().getText(),
                    finder.getOptions());
            newFinder.setReplacement(finder.getReplacement());
        }
    }

}