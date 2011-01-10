package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class ProjectOpenInPreferredApplicationAction extends BaseAction {

	public ProjectOpenInPreferredApplicationAction() {
        setActionStateRules(ActionStateRules.FILE_SELECTED, ActionStateRules.ONE_SELECTION);
	}

	@Override
	public void performAction(ActionEvent e) {
        Object[] tp = actionContext.get(ActionContextKeys.SELECTION);

        try {
            Desktop.getDesktop().open((File) tp[0]);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
