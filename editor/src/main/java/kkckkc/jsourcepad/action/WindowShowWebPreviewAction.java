package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;


public class WindowShowWebPreviewAction extends BaseAction {

    public WindowShowWebPreviewAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC, ActionStateRules.DOC_BACKED_BY_FILE);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
        Doc doc = actionContext.get(ActionContextKeys.ACTIVE_DOC);

        try {
            Desktop.getDesktop().browse(doc.getFile().toURI());
        } catch (IOException e1) {
            e1.printStackTrace();  
        }

    }

}