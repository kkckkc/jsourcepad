
package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;
import java.io.File;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.ProjectPresenter;
import kkckkc.jsourcepad.util.action.BaseAction;

public class FileRevealInProjectAction extends BaseAction {

    private final Window window;

	public FileRevealInProjectAction(Window window, FileSaveAsAction fileSaveAsAction) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC, ActionStateRules.DOC_BACKED_BY_FILE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        Doc doc = actionContext.get(ActionContextKeys.ACTIVE_DOC);
        File file = doc.getFile();

        ProjectPresenter projectPresenter = window.getPresenter(ProjectPresenter.class);
        projectPresenter.revealFile(file);
	}

}
