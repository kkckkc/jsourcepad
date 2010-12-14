package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.model.settings.TabProjectSettings;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.syntaxpane.model.Interval;

import java.awt.event.ActionEvent;

public class TextConvertTabsToSpacesAction extends BaseAction {
    private final Window window;

	public TextConvertTabsToSpacesAction(Window window) {
		this.window = window;
        setActionStateRules(ActionStateRules.HAS_ACTIVE_DOC);
	}

	@Override
    public void actionPerformed(ActionEvent e) {
        TabProjectSettings ts = getSettingsManager().get(TabProjectSettings.class);

		Buffer activeBuffer = window.getDocList().getActiveDoc().getActiveBuffer();

		Interval selectionOrCurrentLine = activeBuffer.getSelectionOrCurrentLine();
        String text = activeBuffer.getText(selectionOrCurrentLine);

        StringBuilder builder = new StringBuilder();
        for (int j = 0; j < ts.getTabSize(); j++) builder.append(" ");

        text = text.replace("\t", builder.toString());

		activeBuffer.replaceText(selectionOrCurrentLine, text, null);
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