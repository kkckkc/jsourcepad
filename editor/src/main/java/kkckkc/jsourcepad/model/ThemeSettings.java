package kkckkc.jsourcepad.model;

import kkckkc.utils.Os;

public class ThemeSettings implements SettingsManager.Setting {

    private String themeId;

    public String getThemeId() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = themeId;
    }

    @Override
    public SettingsManager.Setting getDefault() {
        ThemeSettings ts = new ThemeSettings();
        if (Os.isMac()) {
            ts.setThemeId("theme-osx");
        } else if (Os.isLinux()) {
            ts.setThemeId("theme-gtk");
        } else {
            ts.setThemeId("theme-substance");
        }
        return ts;
    }
}
