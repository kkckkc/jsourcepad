package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.model.settings.StyleSettings;
import kkckkc.jsourcepad.util.action.ActionPresenter;
import kkckkc.jsourcepad.util.action.BaseAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewShowHideInvisiblesAction extends BaseAction implements ActionPresenter.Menu {
    public ViewShowHideInvisiblesAction() {
        updateName();
    }

    @Override
    public void performAction(ActionEvent e) {
        SettingsManager settingsManager = Application.get().getSettingsManager();

        StyleSettings ws = settingsManager.get(StyleSettings.class);
        ws.setShowInvisibles(! ws.isShowInvisibles());
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
        StyleSettings ws = settingsManager.get(StyleSettings.class);
        if (ws.isShowInvisibles()) {
            putValue(NAME, "Hide Invisibles");
        } else {
            putValue(NAME, "Show Invisibles");
        }
    }
}