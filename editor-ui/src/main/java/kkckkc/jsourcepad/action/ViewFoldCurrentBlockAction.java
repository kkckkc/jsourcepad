package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.FoldManager;
import kkckkc.syntaxpane.model.LineManager;

import java.awt.event.ActionEvent;

public class ViewFoldCurrentBlockAction extends BaseAction {

    public ViewFoldCurrentBlockAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
    }

    @Override
    public void performAction(ActionEvent e) {
        Doc doc = actionContext.get(ActionContextKeys.ACTIVE_DOC);
        Buffer buffer = doc.getActiveBuffer();

        FoldManager foldManager = buffer.getFoldManager();

        LineManager.Line line = foldManager.getClosestFoldStart(buffer.getInsertionPoint().getPosition());
        foldManager.toggle(line);
	}

}