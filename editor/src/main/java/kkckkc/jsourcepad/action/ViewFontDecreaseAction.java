package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.FontSettings;
import kkckkc.jsourcepad.model.SettingsManager;
import kkckkc.jsourcepad.util.action.BaseAction;

public class ViewFontDecreaseAction extends BaseAction {

	@Override
    public void actionPerformed(ActionEvent e) {
		SettingsManager settingsManager = Application.get().getSettingsManager();
		
		FontSettings fontSettings = settingsManager.get(FontSettings.class);
		fontSettings.setSize(fontSettings.getSize() - 1);
		settingsManager.update(fontSettings);
	}
	
}
