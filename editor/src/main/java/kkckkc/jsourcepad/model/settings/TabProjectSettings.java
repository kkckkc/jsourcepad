/**
 * 
 */
package kkckkc.jsourcepad.model.settings;

public class TabProjectSettings implements ProjectSetting {
	private boolean softTabs;
	private int tabSize;

	public TabProjectSettings() {
	}
	
	public TabProjectSettings(boolean softTabs, int tabSize) {
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
    public TabProjectSettings getDefault() {
        return new TabProjectSettings(false, 4);
    }
}