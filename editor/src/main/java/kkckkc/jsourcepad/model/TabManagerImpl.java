package kkckkc.jsourcepad.model;

import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.model.settings.TabProjectSettings;
import kkckkc.utils.CharSequenceUtils;



public class TabManagerImpl implements TabManager, SettingsManager.Listener<TabProjectSettings> {
	private boolean softTabs;
	private int tabSize;
	private String softTab;
	
	public TabManagerImpl(Doc doc) {
        if (doc.getDocList().getWindow().getProject() != null) {
            SettingsManager settingsManager = doc.getDocList().getWindow().getProject().getSettingsManager();
            settingsManager.subscribe(TabProjectSettings.class, this, true, Application.get(), doc);
        } else {
            SettingsManager settingsManager = Application.get().getSettingsManager();
		    settingsManager.subscribe(TabProjectSettings.class, this, true, Application.get(), doc);
        }
	}
	
	public void settingUpdated(TabProjectSettings tabSettings) {
		softTabs = tabSettings.isSoftTabs();
		tabSize = tabSettings.getTabSize();

		StringBuilder b = new StringBuilder();
		for (int i = 0; i < tabSize; i++) {
			b.append(" ");
		}
		softTab = b.toString();
	}
	
	@Override
    public String createIndent(int count) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < count; i++) {
			if (softTabs) {
				b.append(softTab);
			} else {
				b.append("\t");
			}
		}
	    return b.toString();
    }

	@Override
    public int getTabCount(CharSequence line) {
		int count = 0;
		String indent;
		while ((indent = getFirstIndentionString(line)) != null) {
			count++;
			line = line.subSequence(indent.length(), line.length());
		}
		return count;
    }

	@Override
    public int getTabSize() {
	    return tabSize;
    }

	@Override
    public String getFirstIndentionString(CharSequence line) {
		if (CharSequenceUtils.startsWith(line, "\t")) {
			return "\t";
		} else if (CharSequenceUtils.startsWith(line, softTab)) {
			return softTab;
		} else {
			return null;
		}
    }

	@Override
	public boolean isSoftTabs() {
	    return softTabs;
	}
}
