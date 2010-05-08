/**
 * 
 */
package kkckkc.jsourcepad.model;

public class TabSettings implements SettingsManager.Setting {
	private boolean softTabs;
	private int tabSize;

	public TabSettings() {
	}
	
	public TabSettings(boolean softTabs, int tabSize) {
        this.softTabs = softTabs;
        this.tabSize = tabSize;
    }

	public boolean isSoftTabs() {
    	return softTabs;
    }

	public void setSoftTabs(boolean softTabs) {
    	this.softTabs = softTabs;
    }

	public int getTabSize() {
    	return tabSize;
    }

	public void setTabSize(int tabSize) {
    	this.tabSize = tabSize;
    }

	@Override
    public TabSettings getDefault() {
        return new TabSettings(false, 4);
    }
}