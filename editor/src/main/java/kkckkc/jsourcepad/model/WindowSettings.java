package kkckkc.jsourcepad.model;

import kkckkc.jsourcepad.model.SettingsManager.Setting;

public class WindowSettings implements Setting {
    private boolean showProjectDrawer;

    public boolean isShowProjectDrawer() {
        return showProjectDrawer;
    }

    public void setShowProjectDrawer(boolean showProjectDrawer) {
        this.showProjectDrawer = showProjectDrawer;
    }

    @Override
    public Setting getDefault() {
        WindowSettings s = new WindowSettings();
        s.showProjectDrawer = true;
        return s;
    }

}