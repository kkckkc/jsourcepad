package kkckkc.jsourcepad.model;

import kkckkc.jsourcepad.model.SettingsManager.Setting;

public class GutterSettings implements Setting {

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
    public Setting getDefault() {
        GutterSettings s = new GutterSettings();
        s.lineNumbers = true;
        s.foldings = true;
        return s;
    }

}