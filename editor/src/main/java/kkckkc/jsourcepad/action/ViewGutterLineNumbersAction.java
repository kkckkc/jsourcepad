package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.GutterSettings;
import kkckkc.jsourcepad.model.SettingsManager;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.jsourcepad.util.action.Presenter;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewGutterLineNumbersAction extends BaseAction implements Presenter.Menu {
    public ViewGutterLineNumbersAction() {
        SettingsManager settingsManager = Application.get().getSettingsManager();

        GutterSettings s = settingsManager.get(GutterSettings.class);
        putValue(Action.SELECTED_KEY, s.isLineNumbers());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SettingsManager settingsManager = Application.get().getSettingsManager();

        GutterSettings s = settingsManager.get(GutterSettings.class);
        s.setLineNumbers(! s.isLineNumbers());
        settingsManager.update(s);

        putValue(Action.SELECTED_KEY, s.isLineNumbers());
    }

    @Override
    public JMenuItem getMenuItem() {
        return new JCheckBoxMenuItem(this);
    }
}