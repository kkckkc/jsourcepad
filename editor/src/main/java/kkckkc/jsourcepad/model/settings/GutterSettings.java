package kkckkc.jsourcepad.model.settings;

public class GutterSettings implements SettingsManager.Setting {

    private boolean lineNumbers;
    private boolean foldings;

    public boolean isLineNumbers() {
        return lineNumbers;
    }

    public void setLineNumbers(boolean lineNumbers) {
        this.lineNumbers = lineNumbers;
    }

    public boolean isFoldings() {
        return foldings;
    }

    public void setFoldings(boolean foldings) {
        this.foldings = foldings;
    }

    @Override
    public SettingsManager.Setting getDefault() {
        GutterSettings s = new GutterSettings();
        s.lineNumbers = true;
        s.foldings = true;
        return s;
    }

}