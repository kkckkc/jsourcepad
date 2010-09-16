
package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;
import javax.swing.Action;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

public class EditorAction extends BaseAction {
    private final Window window;
    private String action;

	public EditorAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

    public void setAction(String action) {
        this.action = action;
    }

	@Override
    public void actionPerformed(ActionEvent e) {
		Doc d = window.getDocList().getActiveDoc();
		Buffer buffer = d.getActiveBuffer();

        Action a = buffer.getActionMap().get(this.action);
        a.actionPerformed(e);
    }

}