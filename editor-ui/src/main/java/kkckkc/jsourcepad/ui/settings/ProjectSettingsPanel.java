package kkckkc.jsourcepad.ui.settings;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.settings.IgnorePatternProjectSettings;
import kkckkc.jsourcepad.model.settings.SettingsPanel;
import kkckkc.jsourcepad.util.Null;

public class ProjectSettingsPanel implements SettingsPanel {
    private ProjectSettingsPanelView view;

    public ProjectSettingsPanel() {
        this.view = new ProjectSettingsPanelView();
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public int getOrder() {
        return 300;
    }

    @Override
    public String getName() {
        return "Project";
    }

    @Override
    public boolean load() {
        Window window = Application.get().getWindowManager().getFocusedWindow();

        if (Null.Utils.isNull(window.getProject())) return false;

        IgnorePatternProjectSettings setting = window.getProject().getSettingsManager().get(IgnorePatternProjectSettings.class);
        view.getExcludePattern().setText(setting.getPattern());

        return true;
    }

    @Override
    public boolean save() {
        Window window = Application.get().getWindowManager().getFocusedWindow();

        if (Null.Utils.isNull(window.getProject())) return false;
        
        IgnorePatternProjectSettings setting = window.getProject().getSettingsManager().get(IgnorePatternProjectSettings.class);
        setting.setPattern(view.getExcludePattern().getText());

        window.getProject().getSettingsManager().update(setting);

        return false;
    }

}
