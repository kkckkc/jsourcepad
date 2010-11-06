package kkckkc.jsourcepad.model.settings;

public class EditModeProjectSettings implements SettingsManager.ProjectSetting {

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
    public SettingsManager.Setting getDefault() {
        EditModeProjectSettings s = new EditModeProjectSettings();
        return s;
    }

}
