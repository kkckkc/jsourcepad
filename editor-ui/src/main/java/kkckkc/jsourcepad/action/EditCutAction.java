package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.ui.DocPresenter;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class EditCutAction extends BaseAction {
	public EditCutAction() {
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
		Doc d = actionContext.get(ActionContextKeys.ACTIVE_DOC);
		DocPresenter dp = d.getPresenter(DocPresenter.class);
		dp.cut();

        Application.get().getClipboardManager().register();
    }

}
