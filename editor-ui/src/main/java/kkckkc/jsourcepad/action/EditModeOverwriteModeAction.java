package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.settings.EditModeProjectSettings;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.jsourcepad.util.action.Presenter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;

public class EditModeOverwriteModeAction extends BaseAction implements Presenter.Menu {
    private Window window;

    @Autowired
    public EditModeOverwriteModeAction(Window window) {
        this.window = window;

        SettingsManager settingsManager = this.window.getProject().getSettingsManager();

        EditModeProjectSettings editModeProjectSettings = settingsManager.get(EditModeProjectSettings.class);
        putValue(Action.SELECTED_KEY, editModeProjectSettings.isOverwriteMode());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
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
