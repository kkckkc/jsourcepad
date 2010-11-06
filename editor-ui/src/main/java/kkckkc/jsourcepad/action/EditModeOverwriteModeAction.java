package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.settings.EditModeProjectSettings;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.jsourcepad.util.action.Presenter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class EditModeOverwriteModeAction extends BaseAction implements Presenter.Menu {
    private Window window;

    @Autowired
    public EditModeOverwriteModeAction(Window window) {
        this.window = window;

        SettingsManager settingsManager = getSettingsManager();

        EditModeProjectSettings s = settingsManager.get(EditModeProjectSettings.class);
        putValue(Action.SELECTED_KEY, s.isOverwriteMode());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SettingsManager settingsManager = getSettingsManager();

        EditModeProjectSettings s = settingsManager.get(EditModeProjectSettings.class);
        s.setOverwriteMode(! s.isOverwriteMode());
        settingsManager.update(s);

        putValue(Action.SELECTED_KEY, s.isOverwriteMode());
    }

    @Override
    public JMenuItem getMenuItem() {
        return new JCheckBoxMenuItem(this);
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
