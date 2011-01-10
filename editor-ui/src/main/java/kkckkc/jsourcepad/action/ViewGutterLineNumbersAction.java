package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.settings.GutterSettings;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.jsourcepad.util.action.Presenter;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewGutterLineNumbersAction extends BaseAction implements Presenter.Menu {
    public ViewGutterLineNumbersAction() {
        SettingsManager settingsManager = Application.get().getSettingsManager();

        GutterSettings settings = settingsManager.get(GutterSettings.class);
        putValue(Action.SELECTED_KEY, settings.isLineNumbers());
    }

    @Override
    public void performAction(ActionEvent e) {
        SettingsManager settingsManager = Application.get().getSettingsManager();

        GutterSettings settings = settingsManager.get(GutterSettings.class);
        settings.setLineNumbers(!settings.isLineNumbers());
        settingsManager.update(settings);

        putValue(Action.SELECTED_KEY, settings.isLineNumbers());
    }

    @Override
    public JMenuItem getMenuItem() {
        return new JCheckBoxMenuItem(this);
    }
}