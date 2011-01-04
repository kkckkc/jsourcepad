package kkckkc.jsourcepad.webview;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import kkckkc.jsourcepad.model.Browser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;

public class WebViewBrowser implements Browser {

    private int numberOfActiveBrowsers = 0;

    public boolean isExternal() {
        return false;
    }

    public void show(URI url, boolean includeNavigation) throws IOException {
        if (! NativeInterface.isOpen()) {
            NativeInterface.open();
        }

        final JFrame frame = new JFrame("Browser");
        frame.getContentPane().add(new WebBrowserPanel(url, includeNavigation), BorderLayout.CENTER);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                numberOfActiveBrowsers--;
                WebViewBrowserLocation.save(frame);
                frame.dispose();
            }
        });

        WebViewBrowserLocation.restore(frame, numberOfActiveBrowsers == 0);

        numberOfActiveBrowsers++;
        frame.setVisible(true);
    }
}
