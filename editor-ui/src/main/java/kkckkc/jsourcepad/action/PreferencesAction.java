package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.ui.dialog.settings.SettingsDialog;
import kkckkc.jsourcepad.util.action.BaseAction;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.event.ActionEvent;

public class PreferencesAction extends BaseAction {
    private SettingsDialog settingsDialog;

    @Autowired
   	public PreferencesAction(SettingsDialog settingsDialog) {
        this.settingsDialog = settingsDialog;
	}

	@Override
    public void performAction(ActionEvent e) {
        settingsDialog.show();
    }

}
