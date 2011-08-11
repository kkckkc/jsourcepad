package kkckkc.jsourcepad.http;

import kkckkc.jsourcepad.command.global.OpenCommand;
import kkckkc.jsourcepad.command.window.FileOpenCommand;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.Config;
import kkckkc.jsourcepad.util.ui.WindowFocusUtils;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.syntaxpane.model.LineManager;
import kkckkc.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class RemoteOpenCommand implements RemoteControl.Command {
    public static final String ID = "open";

    public static final String P_URL = "url";
    public static final String P_WINDOW_ID = "windowId";
    public static final String P_LINE = "line";

    public static URL createRequestForFile(String file) throws MalformedURLException {
        return RemoteControl.buildUrl(ID, P_URL + "=" + file.replace('\\', '/').replace(" ", "+"));
    }

    public String getId() {
        return ID;
    }

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

        String url = req.getParameter(P_URL);
        url = StringUtils.stripPrefix(url, "http://localhost:" + Config.getHttpPort() + "/files");
        url = StringUtils.stripPrefix(url, "file://");

        Window window;
        if (req.getParameter(P_WINDOW_ID) != null) {
            int windowId = Integer.parseInt(req.getParameter(P_WINDOW_ID));

            window = Application.get().getWindowManager().getWindow(windowId);
            window.getCommandExecutor().executeSync(new FileOpenCommand(url));
        } else {
            OpenCommand openCommand = new OpenCommand(url, false);
            Application.get().getCommandExecutor().executeSync(openCommand);
            window = openCommand.getWindow();
        }

        // Trigger toFront
        WindowFocusUtils.focusWindow(window.getContainer());

        String line = req.getParameter(P_LINE);
        if (line != null) {
            int lineIdx = Integer.parseInt(line);

            Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();
            LineManager lm = buffer.getLineManager();

            Iterator<LineManager.Line> it = lm.iterator();
            while (it.hasNext()) {
                LineManager.Line l = it.next();
                if (l.getIdx() == (lineIdx - 1)) {
                    buffer.setSelection(Interval.createEmpty(l.getStart()));
                    break;
                }
            }
        }
    }
}
