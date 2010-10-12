package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.ui.ProjectPresenter;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;
import java.io.File;

public class FileRevealInProjectAction extends BaseAction {

	public FileRevealInProjectAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC, ActionStateRules.DOC_BACKED_BY_FILE, ActionStateRules.HAS_PROJECT);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        Doc doc = actionContext.get(ActionContextKeys.ACTIVE_DOC);
        File file = doc.getFile();

        ProjectPresenter projectPresenter = doc.getDocList().getWindow().getPresenter(ProjectPresenter.class);
        projectPresenter.revealFile(file);
	}

}
