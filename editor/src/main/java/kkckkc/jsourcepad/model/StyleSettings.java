package kkckkc.jsourcepad.model;

import kkckkc.jsourcepad.model.SettingsManager.Setting;

public class StyleSettings implements Setting {

	private String themeLocation;
	
	public StyleSettings() {
	}
	
	public StyleSettings(String themeLocation) {
	    this.themeLocation = themeLocation;
    }

	public String getThemeLocation() {
    	return themeLocation;
    }

	public void setThemeLocation(String themeLocation) {
    	this.themeLocation = themeLocation;
    }

	@Override
    public StyleSettings getDefault() {
	    // TODO Change this
	    return new StyleSettings("/home/magnus/Dropbox/SharedSupport/krTheme.tmTheme");
    }

}
