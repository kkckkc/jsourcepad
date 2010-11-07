package kkckkc.jsourcepad.model.settings;

public class IgnorePatternProjectSettings implements SettingsManager.ProjectSetting {
    private String pattern;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public SettingsManager.Setting getDefault() {
        IgnorePatternProjectSettings ignore = new IgnorePatternProjectSettings();
        ignore.setPattern("\\..+|.+\\.class|.+\\.o");
        return ignore;
    }
}
