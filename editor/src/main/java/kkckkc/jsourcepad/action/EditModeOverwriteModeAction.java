
package kkckkc.jsourcepad.action;

import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.EditModeSettings;
import kkckkc.jsourcepad.model.SettingsManager;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.jsourcepad.util.action.Presenter;

public class EditModeOverwriteModeAction extends BaseAction implements Presenter.Menu {
	public EditModeOverwriteModeAction() {
        SettingsManager settingsManager = Application.get().getSettingsManager();

        EditModeSettings s = settingsManager.get(EditModeSettings.class);
        putValue(Action.SELECTED_KEY, s.isOverwriteMode());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SettingsManager settingsManager = Application.get().getSettingsManager();

        EditModeSettings s = settingsManager.get(EditModeSettings.class);
        s.setOverwriteMode(! s.isOverwriteMode());
        settingsManager.update(s);

        putValue(Action.SELECTED_KEY, s.isOverwriteMode());
    }

    @Override
    public JMenuItem getMenuItem() {
        return new JCheckBoxMenuItem(this);
    }
}
