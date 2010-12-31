package kkckkc.jsourcepad.webview;

import chrriis.dj.nativeswing.NativeSwing;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import kkckkc.jsourcepad.model.Browser;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebViewBrowser implements Browser {

    public boolean isExternal() {
        return false;
    }

    public void show(URI url, boolean includeNavigation) throws IOException {
        if (! NativeInterface.isOpen()) {
            NativeInterface.open();
        }

        JFrame frame = new JFrame("Browser");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(new WebBrowserPanel(url, includeNavigation), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }


    public static void main(String... args) {
        NativeSwing.initialize();
        NativeInterface.initialize();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    new WebViewBrowser().show(new URI("http://www.idg.se"), false);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        NativeInterface.runEventPump();
    }
}
