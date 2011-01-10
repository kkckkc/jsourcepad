package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.util.Config;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WindowShowWebPreviewAction extends BaseAction {

    public WindowShowWebPreviewAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
    }

	@Override
	public void performAction(ActionEvent e) {
        final Doc doc = actionContext.get(ActionContextKeys.ACTIVE_DOC);

        String path = "/preview/" + doc.getDocList().getWindow().getId();

        if (doc.isBackedByFile()) {
            // TODO: Fix this when file is not in project window
            path += doc.getDocList().getWindow().getProject().getProjectRelativePath(doc.getFile().getPath());
        } else {
            path += "/tab-" + doc.getDocList().getIndex(doc);
        }

        try {
            Application.get().getBrowser().show(new URI("http://localhost:" + Config.getHttpPort() + path.replace("\\", "/")), true);
        } catch (IOException e1) {
            e1.printStackTrace();  
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }

    }

}