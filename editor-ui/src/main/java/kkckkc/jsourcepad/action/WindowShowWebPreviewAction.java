package kkckkc.jsourcepad.action;

import com.sun.net.httpserver.HttpServer;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WindowShowWebPreviewAction extends BaseAction {

    public WindowShowWebPreviewAction() {
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
        final Doc doc = actionContext.get(ActionContextKeys.ACTIVE_DOC);

        String path = "/preview/" + doc.getDocList().getWindow().getId();

        if (doc.isBackedByFile()) {
            path += doc.getDocList().getWindow().getProject().getProjectRelativePath(doc.getFile().getPath());
        } else {
            path += "/tab-" + doc.getDocList().getIndex(doc);
        }

        final HttpServer server = Application.get().getHttpServer();


        try {
            Desktop.getDesktop().browse(new URI("http://localhost:" + server.getAddress().getPort() + path));
        } catch (IOException e1) {
            e1.printStackTrace();  
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }

    }

}