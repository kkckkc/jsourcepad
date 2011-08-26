package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.model.settings.StyleSettings;
import kkckkc.jsourcepad.util.action.ActionPresenter;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.jsourcepad.util.messagebus.Subscription;

import javax.annotation.PreDestroy;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewWrapColumnAction extends BaseAction implements ActionPresenter.Menu, SettingsManager.Listener<StyleSettings> {
    private int column;

    private Subscription subscription;

    public ViewWrapColumnAction() {
    }

    public void setColumn(int column) {
        this.column = column;

        if (column != 0) {
            SettingsManager settingsManager = Application.get().getSettingsManager();
            subscription = settingsManager.subscribe(StyleSettings.class, this, true);
        }

        updateName();
    }

    @PreDestroy
    public void destroy() {
        subscription.unsubscribe();
    }

    @Override
    public void performAction(ActionEvent e) {
        int newValue = column;
        if (column == 0) {
            String s = JOptionPane.showInputDialog("Enter column width");
            try {
                newValue = Integer.parseInt(s);
            } catch (NumberFormatException nfe) {
                return;
            }
        }

        SettingsManager settingsManager = Application.get().getSettingsManager();

        StyleSettings ws = settingsManager.get(StyleSettings.class);
        ws.setWrapColumn(newValue);
        settingsManager.update(ws);

        updateName();
    }

    @Override
    public JMenuItem getMenuItem() {
        updateName();
        if (column == 0) return new JMenuItem(this);

        return new JCheckBoxMenuItem(this);
    }

    private void updateName() {
        if (column == 0) {
            SettingsManager settingsManager = Application.get().getSettingsManager();
            StyleSettings ws = settingsManager.get(StyleSettings.class);
            if (ws.getWrapColumn() == 78 || ws.getWrapColumn() == 40) {
                putValue(NAME, "Other...");
            } else {
                putValue(NAME, "Other (" + ws.getWrapColumn() + ")...");
            }
        } else {
            putValue(NAME, Integer.toString(column));
        }
    }

    @Override
    public void settingUpdated(StyleSettings settings) {
        if (column == 0) return;
        putValue(SELECTED_KEY, column == settings.getWrapColumn());
    }
}