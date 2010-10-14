package kkckkc.jsourcepad.ui.settings;

import kkckkc.jsourcepad.model.SettingsPanel;

public class StyleSettingsPanel implements SettingsPanel {
    private StyleSettingsPanelView view;

    public StyleSettingsPanel() {
        this.view = new StyleSettingsPanelView();
    }
    
    @Override
    public View getView() {
        return view;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String getName() {
        return "Font & Colors";
    }

    @Override
    public void load() {
    }

    @Override
    public boolean save() {
        return false;  
    }
}
