package kkckkc.jsourcepad.ui.settings;

import kkckkc.jsourcepad.Plugin;
import kkckkc.jsourcepad.PluginManager;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.SettingsManager;
import kkckkc.jsourcepad.model.SettingsPanel;
import kkckkc.jsourcepad.model.ThemeSettings;
import kkckkc.jsourcepad.theme.DefaultTheme;
import kkckkc.jsourcepad.theme.Theme;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ThemeSettingsPanel implements SettingsPanel {

    private ThemeSettingsPanelView view;

    public ThemeSettingsPanel() {
        this.view = new ThemeSettingsPanelView();

        view.getThemes().addItem(new DefaultTheme().getId());
        for (Plugin p : PluginManager.getAllPlugins()) {
            if (! (p instanceof Theme)) continue;
            if (! ((Theme) p).isAvailable()) continue;

            view.getThemes().addItem(((Theme) p).getId());
        }

        view.getThemes().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Theme t = getSelectedTheme();

                JPanel panel = view.getThemePanel();
                panel.removeAll();

                if (t.getSettingsPanel() != null) {
                    t.getSettingsPanel().load();
                    panel.add(t.getSettingsPanel().getView().getJPanel());
                }
            }
        });
    }

    protected Theme getSelectedTheme() {
        for (Plugin p : PluginManager.getAllPlugins()) {
            if (!(p instanceof Theme)) continue;

            Theme t = (Theme) p;


            if (t.getId().equals(view.getThemes().getSelectedItem())) {
                return t;
            }
        }

        return new DefaultTheme();
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
        view.getThemes().setSelectedItem(Application.get().getTheme().getId());

        if (getSelectedTheme().getSettingsPanel() != null) {
            getSelectedTheme().getSettingsPanel().load();
            view.getThemePanel().add(getSelectedTheme().getSettingsPanel().getView().getJPanel());
        }
    }

    @Override
    public boolean save() {
        SettingsManager sm = Application.get().getSettingsManager();
        ThemeSettings ts = sm.get(ThemeSettings.class);
        
        boolean restartRequired = ! view.getThemes().getSelectedItem().equals(Application.get().getTheme().getId());

        ts.setThemeId((String) view.getThemes().getSelectedItem());

        sm.update(ts);

        Theme t = getSelectedTheme();
        if (t.getSettingsPanel() != null) {
            restartRequired |= t.getSettingsPanel().save();
        }

        return restartRequired;
    }
}
