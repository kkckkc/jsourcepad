package kkckkc.jsourcepad.ui.settings;

import kkckkc.jsourcepad.model.SettingsPanel;

public class ThemeSettingsPanel implements SettingsPanel {

    private ThemeSettingsPanelView view;

    public ThemeSettingsPanel() {
        this.view = new ThemeSettingsPanelView();
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public String getName() {
        return "Theme";
    }

    @Override
    public void load() {
    }

    @Override
    public boolean save() {
        return false;  
    }
}
