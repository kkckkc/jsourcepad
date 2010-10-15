package kkckkc.jsourcepad.theme;

import kkckkc.jsourcepad.model.SettingsManager;

public class SubstanceSettings implements SettingsManager.Setting {

    private String skin;

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    @Override
    public SettingsManager.Setting getDefault() {
        SubstanceSettings ss = new SubstanceSettings();
        ss.setSkin(null);
        return ss;
    }
}
