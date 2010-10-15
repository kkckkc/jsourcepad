package kkckkc.jsourcepad.model;

import kkckkc.utils.EnvironmentUtils;

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
        if (EnvironmentUtils.isMac()) {
            ts.setThemeId("theme-osx");
        } else {
            ts.setThemeId(null);
        }
        return ts;
    }
}
