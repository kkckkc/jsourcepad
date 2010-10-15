package kkckkc.jsourcepad.theme;

import kkckkc.jsourcepad.model.SettingsManager;
import kkckkc.jsourcepad.model.SettingsPanel;
import kkckkc.jsourcepad.model.ThemeSettings;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.springframework.core.io.Resource;

import javax.swing.*;

public class SubstanceTheme implements Theme {

    private SubstanceSettingsPanel settingsPanel;

    @Override
	public Object getLookAndFeel() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SubstanceSettings ss = SettingsManager.GLOBAL.get(SubstanceSettings.class);
                if (ss.getSkin() != null) {
                    SubstanceLookAndFeel.setSkin(ss.getSkin());
                }
              }
        });

        return new SubstanceBusinessBlackSteelLookAndFeel();
	}

    @Override
    public void activate() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
    }

    @Override
    public SettingsPanel getSettingsPanel() {
        if (this.settingsPanel == null) {
            this.settingsPanel = new SubstanceSettingsPanel();
        }
        return this.settingsPanel;
    }

    @Override
    public String getId() {
        return "theme-substance";
    }

    @Override
    public String[] getDependsOn() {
        return new String[] { "editor-ui" };
    }

    @Override
    public boolean isEnabled() {
        ThemeSettings ts = SettingsManager.GLOBAL.get(ThemeSettings.class);
        return ts.getThemeId() != null && ts.getThemeId().equals(getId());
    }

    @Override
    public Resource getOverridesLocation(BeanFactoryLoader.Scope<?> scope) {
		return null;
    }

}