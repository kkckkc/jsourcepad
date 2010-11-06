package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.settings.GutterSettings;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.jsourcepad.util.action.Presenter;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewGutterFoldingsAction extends BaseAction implements Presenter.Menu {
	public ViewGutterFoldingsAction() {
        SettingsManager settingsManager = Application.get().getSettingsManager();

        GutterSettings s = settingsManager.get(GutterSettings.class);
        putValue(Action.SELECTED_KEY, s.isFoldings());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SettingsManager settingsManager = Application.get().getSettingsManager();

        GutterSettings s = settingsManager.get(GutterSettings.class);
        s.setFoldings(! s.isFoldings());
        settingsManager.update(s);

        putValue(Action.SELECTED_KEY, s.isFoldings());
    }

    @Override
    public JMenuItem getMenuItem() {
        return new JCheckBoxMenuItem(this);
    }
}