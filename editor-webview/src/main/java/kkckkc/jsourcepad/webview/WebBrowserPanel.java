package kkckkc.jsourcepad.webview;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserFunction;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.bundle.EnvironmentProvider;
import kkckkc.jsourcepad.util.io.ScriptExecutor;
import kkckkc.jsourcepad.util.io.UISupportCallback;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class WebBrowserPanel extends JPanel {

    public WebBrowserPanel(URI url, boolean showNavigation) throws MalformedURLException {
        super(new BorderLayout());

        JPanel webBrowserPanel = new JPanel(new BorderLayout());

        final JWebBrowser webBrowser = new JWebBrowser();
        webBrowser.setMenuBarVisible(false);
        webBrowser.setLocationBarVisible(false);
        webBrowser.setButtonBarVisible(showNavigation);
        webBrowser.setStatusBarVisible(true);
        webBrowser.navigate(url.toURL().toExternalForm());
        webBrowserPanel.add(webBrowser, BorderLayout.CENTER);

        webBrowser.registerFunction(new WebBrowserFunction("_system") {
            @Override
            public Object invoke(final JWebBrowser jWebBrowser, Object... objects) {
                final String event = (String) objects[0];
                final String cmd = (String) objects[1];
                final String callback = (String) objects[2];

                System.out.println("event = " + event);
                System.out.println("cmd = " + cmd);
                System.out.println("callback = " + callback);
                System.out.println("--------------------------------------------------");

                if (callback == null) {
                    String output = execute(cmd);
                    return new Object[] { output, "", 0 };
                } else {
                    Application.get().getThreadPool().submit(new Runnable() {
                        @Override
                        public void run() {
                            String output = execute(cmd);
                            jWebBrowser.executeJavascript(JWebBrowser.createJavascriptFunctionCall("TextMate.callback." + callback, output, "", 0));
                        }
                    });
                    return new Object[] { null, null, null };
                }
            }
        });

        add(webBrowserPanel, BorderLayout.CENTER);
    }

    private String execute(String cmd) {
        try {
            Window window = Application.get().getWindowManager().getFocusedWindow();
            window.beginWait(true, null);

            Map<String,String> environment = EnvironmentProvider.getEnvironment(window, null);

            ScriptExecutor scriptExecutor = new ScriptExecutor(cmd, Application.get().getThreadPool());
            scriptExecutor.setDelay(0);
            scriptExecutor.setShowStderr(false);
            ScriptExecutor.Execution execution = scriptExecutor.execute(
                    new UISupportCallback(window),
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

            return execution.getStdout();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

}
