package kkckkc.jsourcepad.model.settings;

import kkckkc.jsourcepad.model.settings.SettingsManager.Setting;

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
        WindowSettings settings = new WindowSettings();
        settings.showProjectDrawer = true;
        return settings;
    }

}