
package kkckkc.jsourcepad.action;

import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.ClipboardManager;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

public class EditPastePreviousAction extends BaseAction {
    private final Window window;

	public EditPastePreviousAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void actionPerformed(ActionEvent e) {
		Doc d = window.getDocList().getActiveDoc();

        ClipboardManager cm = Application.get().getClipboardManager();
        Transferable t = cm.getSecondLast();

        if (t == null) return;
        
        Buffer b = d.getActiveBuffer();
        b.insertText(b.getInsertionPoint().getPosition(), 
                ClipboardManager.getText(t), null);
    }

}
