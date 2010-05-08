package kkckkc.jsourcepad.model;

public interface TabManager {
	public String getFirstIndentionString(CharSequence line);
	public int getTabCount(CharSequence line);
	public String createIndent(int count);
//	
	public int getTabSize();
//	public void setTabSize(int size);
//
	public boolean isSoftTabs();
//	public void setSoftTabs(boolean selected);
}
