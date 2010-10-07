package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.util.action.BaseAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class EditorAction extends BaseAction {
    private String action;

	public EditorAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

    public void setAction(String action) {
        this.action = action;
    }

	@Override
    public void actionPerformed(ActionEvent e) {
        Doc d = actionContext.get(ActionContextKeys.ACTIVE_DOC);
		Buffer buffer = d.getActiveBuffer();

        Action a = buffer.getActionMap().get(this.action);
        a.actionPerformed(e);
    }

}