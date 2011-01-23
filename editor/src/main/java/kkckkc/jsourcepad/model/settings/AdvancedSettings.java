package kkckkc.jsourcepad.model.settings;

public class AdvancedSettings implements SettingsManager.Setting {
    private String wordPattern;

    public String getWordPattern() {
        return wordPattern;
    }

    public void setWordPattern(String wordPattern) {
        this.wordPattern = wordPattern;
    }

    @Override
    public SettingsManager.Setting getDefault() {
        AdvancedSettings as = new AdvancedSettings();
        as.setWordPattern("[\\p{Alpha}\\p{Digit}_]+");
        return as;
    }
}
