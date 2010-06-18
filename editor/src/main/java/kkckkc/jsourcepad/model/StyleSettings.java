package kkckkc.jsourcepad.model;

import kkckkc.jsourcepad.model.SettingsManager.Setting;
import kkckkc.jsourcepad.util.ApplicationFolder;

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
	    return new StyleSettings(ApplicationFolder.get("Shared/Themes/Mac Classic.tmTheme").toString());
    }

}
