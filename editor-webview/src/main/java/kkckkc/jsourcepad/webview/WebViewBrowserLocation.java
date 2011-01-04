package kkckkc.jsourcepad.webview;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.settings.SettingsManager;

import javax.swing.*;
import java.awt.*;


public class WebViewBrowserLocation implements SettingsManager.Setting {
    private Point location;
    private Dimension dimensions;

    public WebViewBrowserLocation() {
    }

    public WebViewBrowserLocation(Point location, Dimension dimensions) {
        this.location = location;
        this.dimensions = dimensions;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public Dimension getDimensions() {
        return dimensions;
    }

    public void setDimensions(Dimension dimensions) {
        this.dimensions = dimensions;
    }

    public static void restore(JFrame jframe, boolean setPosition) {
        WebViewBrowserLocation w = Application.get().getSettingsManager().get(WebViewBrowserLocation.class);
        jframe.setSize(w.getDimensions());
        if (w.getLocation() == null || !setPosition) {
            jframe.setLocationByPlatform(true);
        } else {
            jframe.setLocation(w.getLocation());
        }
    }

    public static void save(JFrame jframe) {
        SettingsManager settingsManager = Application.get().getSettingsManager();

        WebViewBrowserLocation w = settingsManager.get(WebViewBrowserLocation.class);
        w.setDimensions(jframe.getSize());
        w.setLocation(jframe.getLocation());
        settingsManager.update(w);
    }

    @Override
    public SettingsManager.Setting getDefault() {
        return new WebViewBrowserLocation(null, new Dimension(800, 600));
    }
}
