package kkckkc.jsourcepad.theme.substance;

import kkckkc.jsourcepad.model.settings.SettingsManager;

public class SubstanceSettings implements SettingsManager.Setting {

    private String skin;
    private int fontSizeAdjustment;
    private boolean keepMenuSize;
    private boolean decorateWindowControls;

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public int getFontSizeAdjustment() {
        return fontSizeAdjustment;
    }

    public void setFontSizeAdjustment(int fontSizeAdjustment) {
        this.fontSizeAdjustment = fontSizeAdjustment;
    }

    public boolean isKeepMenuSize() {
        return keepMenuSize;
    }

    public void setKeepMenuSize(boolean keepMenuSize) {
        this.keepMenuSize = keepMenuSize;
    }

    @Override
    public SettingsManager.Setting getDefault() {
        SubstanceSettings ss = new SubstanceSettings();
        ss.setSkin(null);
        ss.setKeepMenuSize(true);
        ss.setFontSizeAdjustment(0);
        ss.setDecorateWindowControls(false);
        return ss;
    }

    public boolean isDecorateWindowControls() {
        return decorateWindowControls;
    }

    public void setDecorateWindowControls(boolean decorateWindowControls) {
        this.decorateWindowControls = decorateWindowControls;
    }
}
