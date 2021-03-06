package kkckkc.jsourcepad.model;

import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.model.settings.TabProjectSettings;
import kkckkc.jsourcepad.util.messagebus.Subscription;
import kkckkc.syntaxpane.model.TabManager;
import kkckkc.utils.CharSequenceUtils;

import javax.annotation.PreDestroy;


public class TabManagerImpl implements TabManager, SettingsManager.Listener<TabProjectSettings> {
	private boolean softTabs;
	private int tabSize;
	private String softTab;

    private Subscription subscription;

	public TabManagerImpl(Doc doc) {
        SettingsManager settingsManager = doc.getDocList().getWindow().getProject().getSettingsManager();
        subscription = settingsManager.subscribe(TabProjectSettings.class, this, true);
	}

    @PreDestroy
    public void destroy() {
        subscription.unsubscribe();
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
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < count; i++) {
			if (softTabs) {
				builder.append(softTab);
			} else {
				builder.append("\t");
			}
		}
	    return builder.toString();
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
