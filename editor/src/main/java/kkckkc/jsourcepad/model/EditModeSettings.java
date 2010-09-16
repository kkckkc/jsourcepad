
package kkckkc.jsourcepad.model;

import kkckkc.jsourcepad.model.SettingsManager.Setting;

public class EditModeSettings implements Setting {

    private boolean overwriteMode;
    private boolean freehandMode;

    public boolean isFreehandMode() {
        return freehandMode;
    }

    public void setFreehandMode(boolean freehandMode) {
        this.freehandMode = freehandMode;
    }

    public boolean isOverwriteMode() {
        return overwriteMode;
    }

    public void setOverwriteMode(boolean overwriteMode) {
        this.overwriteMode = overwriteMode;
    }

    @Override
    public Setting getDefault() {
        EditModeSettings s = new EditModeSettings();
        return s;
    }

}
