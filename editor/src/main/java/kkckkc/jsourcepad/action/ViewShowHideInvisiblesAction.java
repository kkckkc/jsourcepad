package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.SettingsManager;
import kkckkc.jsourcepad.model.StyleSettings;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.jsourcepad.util.action.Presenter;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewShowHideInvisiblesAction extends BaseAction implements Presenter.Menu {
    public ViewShowHideInvisiblesAction() {
        updateName();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
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