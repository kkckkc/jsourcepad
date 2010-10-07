package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.ClipboardManager;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

public class EditPastePreviousAction extends BaseAction {
	public EditPastePreviousAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void actionPerformed(ActionEvent e) {
        Doc d = actionContext.get(ActionContextKeys.ACTIVE_DOC);

        ClipboardManager cm = Application.get().getClipboardManager();
        Transferable t = cm.getSecondLast();

        if (t == null) return;
        
        Buffer b = d.getActiveBuffer();
        b.insertText(b.getInsertionPoint().getPosition(), 
                ClipboardManager.getText(t), null);
    }

}
