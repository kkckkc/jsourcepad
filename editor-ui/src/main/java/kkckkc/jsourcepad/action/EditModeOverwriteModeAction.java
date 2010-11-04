package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.EditModeSettings;
import kkckkc.jsourcepad.model.SettingsManager;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.jsourcepad.util.action.Presenter;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class EditModeOverwriteModeAction extends BaseAction implements Presenter.Menu {
	public EditModeOverwriteModeAction() {
        SettingsManager settingsManager = Application.get().getSettingsManager();

        EditModeSettings s = settingsManager.get(EditModeSettings.class);
        putValue(Action.SELECTED_KEY, s.isOverwriteMode());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SettingsManager settingsManager = Application.get().getSettingsManager();

        EditModeSettings s = settingsManager.get(EditModeSettings.class);
        s.setOverwriteMode(! s.isOverwriteMode());
        settingsManager.update(s);

        putValue(Action.SELECTED_KEY, s.isOverwriteMode());
    }

    @Override
    public JMenuItem getMenuItem() {
        return new JCheckBoxMenuItem(this);
    }
}
