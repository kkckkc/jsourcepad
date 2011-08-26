package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.settings.EditModeProjectSettings;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.util.action.ActionPresenter;
import kkckkc.jsourcepad.util.action.BaseAction;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class EditModeOverwriteModeAction extends BaseAction implements ActionPresenter.Menu {
    private Window window;

    @Autowired
    public EditModeOverwriteModeAction(Window window) {
        this.window = window;

        SettingsManager settingsManager = this.window.getProject().getSettingsManager();

        EditModeProjectSettings editModeProjectSettings = settingsManager.get(EditModeProjectSettings.class);
        putValue(Action.SELECTED_KEY, editModeProjectSettings.isOverwriteMode());
    }

    @Override
    public void performAction(ActionEvent e) {
        SettingsManager settingsManager = window.getProject().getSettingsManager();

        EditModeProjectSettings editModeProjectSettings = settingsManager.get(EditModeProjectSettings.class);
        editModeProjectSettings.setOverwriteMode(!editModeProjectSettings.isOverwriteMode());
        settingsManager.update(editModeProjectSettings);

        putValue(Action.SELECTED_KEY, editModeProjectSettings.isOverwriteMode());
    }

    @Override
    public JMenuItem getMenuItem() {
        return new JCheckBoxMenuItem(this);
    }
}
