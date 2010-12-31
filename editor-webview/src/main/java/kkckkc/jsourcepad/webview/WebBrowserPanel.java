package kkckkc.jsourcepad.webview;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URI;

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
        add(webBrowserPanel, BorderLayout.CENTER);
    }

}
