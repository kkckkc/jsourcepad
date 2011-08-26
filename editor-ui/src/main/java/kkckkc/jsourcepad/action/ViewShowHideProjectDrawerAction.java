package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.model.settings.WindowSettings;
import kkckkc.jsourcepad.util.action.ActionPresenter;
import kkckkc.jsourcepad.util.action.BaseAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewShowHideProjectDrawerAction extends BaseAction implements ActionPresenter.Menu {
    public ViewShowHideProjectDrawerAction() {
        updateName();
        setActionStateRules(ActionStateRules.HAS_PROJECT);
    }

    @Override
    public void performAction(ActionEvent e) {
        SettingsManager settingsManager = Application.get().getSettingsManager();

        WindowSettings ws = settingsManager.get(WindowSettings.class);
        ws.setShowProjectDrawer(! ws.isShowProjectDrawer());
        settingsManager.update(ws);

        updateName();
    }

    @Override
    public JMenuItem getMenuItem() {
        updateName();
        return new JMenuItem(this);
    }

    private void updateName() {
        SettingsManager settingsManager = Application.get().getSettingsManager();
        WindowSettings ws = settingsManager.get(WindowSettings.class);
        if (ws.isShowProjectDrawer()) {
            putValue(NAME, "Hide Project Drawer");
        } else {
            putValue(NAME, "Show Project Drawer");
        }
    }
}