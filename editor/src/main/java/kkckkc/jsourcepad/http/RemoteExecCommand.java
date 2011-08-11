package kkckkc.jsourcepad.http;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.bundle.EnvironmentProvider;
import kkckkc.jsourcepad.util.io.ScriptExecutor;
import kkckkc.jsourcepad.util.io.UISupportCallback;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RemoteExecCommand implements RemoteControl.Command {
    public static final String ID = "exec";
    public static final String P_CMD = "cmd";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) {
        final String cmdString = req.getParameter(P_CMD);

        try {
            Window window = Application.get().getWindowManager().getFocusedWindow();
            window.beginWait(true, null);

            Map<String,String> environment = EnvironmentProvider.getEnvironment(window, null);
            UISupportCallback callback = new UISupportCallback(window);

            ScriptExecutor scriptExecutor = new ScriptExecutor(cmdString, Application.get().getThreadPool());
            scriptExecutor.setDelay(0);
            scriptExecutor.setShowStderr(false);
            ScriptExecutor.Execution execution = scriptExecutor.execute(
                    callback,
                    new StringReader(""),
                    environment);
            try {
                execution.waitForCompletion();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } finally {
                window.endWait();
            }

            resp.getWriter().write(execution.getStdout());
            resp.getWriter().flush();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
