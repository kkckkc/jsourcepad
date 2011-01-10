package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.settings.FontSettings;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class ViewFontIncreaseAction extends BaseAction {

	@Override
    public void performAction(ActionEvent e) {
		SettingsManager settingsManager = Application.get().getSettingsManager();
		
		FontSettings fontSettings = settingsManager.get(FontSettings.class);
		fontSettings.setSize(fontSettings.getSize() + 1);
		settingsManager.update(fontSettings);
	}
	
}
