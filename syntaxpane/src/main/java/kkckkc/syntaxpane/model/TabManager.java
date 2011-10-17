package kkckkc.syntaxpane.model;

public interface TabManager {
	public String getFirstIndentionString(CharSequence line);
	public int getTabCount(CharSequence line);
	public String createIndent(int count);
	public int getTabSize();
	public boolean isSoftTabs();
}
