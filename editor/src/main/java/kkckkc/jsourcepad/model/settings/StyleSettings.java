package kkckkc.jsourcepad.model.settings;

import kkckkc.jsourcepad.model.settings.SettingsManager.Setting;

public class StyleSettings implements Setting {

	private String themeLocation;
	private boolean showInvisibles;
    private int wrapColumn;

	public StyleSettings() {
	}
	
	public StyleSettings(String themeLocation, boolean showInvisibles, int wrapColumn) {
	    this.themeLocation = themeLocation;
        this.showInvisibles = showInvisibles;
        this.wrapColumn = wrapColumn;
    }

    public boolean isShowInvisibles() {
        return showInvisibles;
    }

    public void setShowInvisibles(boolean showInvisibles) {
        this.showInvisibles = showInvisibles;
    }

    public String getThemeLocation() {
    	return themeLocation;
    }

	public void setThemeLocation(String themeLocation) {
    	this.themeLocation = themeLocation;
    }

    public int getWrapColumn() {
        return wrapColumn;
    }

    public void setWrapColumn(int wrapColumn) {
        this.wrapColumn = wrapColumn;
    }

    @Override
    public StyleSettings getDefault() {
	    return new StyleSettings("Twilight.tmTheme", true, 78);
    }

}
