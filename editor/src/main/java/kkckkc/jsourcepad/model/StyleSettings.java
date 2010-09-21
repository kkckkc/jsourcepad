package kkckkc.jsourcepad.model;

import kkckkc.jsourcepad.model.SettingsManager.Setting;
import kkckkc.jsourcepad.util.ApplicationFolder;

public class StyleSettings implements Setting {

	private String themeLocation;
	private boolean showInvisibles;

	public StyleSettings() {
	}
	
	public StyleSettings(String themeLocation, boolean showInvisibles) {
	    this.themeLocation = themeLocation;
        this.showInvisibles = showInvisibles;
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

	@Override
    public StyleSettings getDefault() {
	    return new StyleSettings(ApplicationFolder.get("Shared/Themes/Mac Classic.tmTheme").toString(), true);
    }

}
