package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

public class NavigationGotoHeaderSourceAction extends BaseAction {

	public NavigationGotoHeaderSourceAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC, ActionStateRules.DOC_BACKED_BY_FILE, ActionStateRules.HAS_PROJECT);
	}

	@Override
    public void actionPerformed(ActionEvent e) {
		Doc doc = actionContext.get(ActionContextKeys.ACTIVE_DOC);

        String fileName = doc.getFile().getName();
        int idx = fileName.lastIndexOf(".");
        if (idx >= 0) fileName = fileName.substring(0, idx - 1);

        List<File> files = doc.getDocList().getWindow().getProject().findFile(fileName);

        File found = null;

        boolean select = false;
        for (File file : files) {
            if (select) {
                found = file;
                break;
            } else if (file.equals(doc.getFile())) {
                select = true;
            }
        }

        if (found != null) {
            doc.getDocList().open(found);
        }
    }

}