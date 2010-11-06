package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.model.settings.TabProjectSettings;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.TextInterval;

import java.awt.event.ActionEvent;

public class TextConvertSpacesToTabsAction extends BaseAction {
    private final Window window;

	public TextConvertSpacesToTabsAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void actionPerformed(ActionEvent e) {
        TabProjectSettings ts = getSettingsManager().get(TabProjectSettings.class);

		Buffer b = window.getDocList().getActiveDoc().getActiveBuffer();

        TextInterval i = b.getSelectionOrCurrentLine();
        String text = i.getText();

        StringBuilder builder = new StringBuilder();
        for (int j = 0; j < ts.getTabSize(); j++) builder.append(" ");

        text = text.replace(builder.toString(), "\t");

		b.replaceText(i, text, null);
    }

    private SettingsManager getSettingsManager() {
        SettingsManager settingsManager;
        if (window.getProject() != null) {
            settingsManager = window.getProject().getSettingsManager();
        } else {
            settingsManager = Application.get().getSettingsManager();
        }
        return settingsManager;
    }
}