package kkckkc.jsourcepad.theme.substance;

import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.model.settings.SettingsPanel;
import kkckkc.jsourcepad.model.settings.ThemeSettings;
import kkckkc.jsourcepad.theme.Theme;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.fonts.FontPolicy;
import org.pushingpixels.substance.api.fonts.FontSet;
import org.pushingpixels.substance.api.skin.SubstanceOfficeBlack2007LookAndFeel;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.swing.*;

public class SubstanceTheme implements Theme {

    private SubstanceSettingsPanel settingsPanel;

    @Override
	public Object getLookAndFeel() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final SubstanceSettings ss = SettingsManager.GLOBAL.get(SubstanceSettings.class);
                if (ss.getSkin() != null) {
                    SubstanceLookAndFeel.setSkin(ss.getSkin());

                    SubstanceLookAndFeel.setFontPolicy(null);
                    // Get the default font set
                    final FontSet substanceCoreFontSet = SubstanceLookAndFeel.getFontPolicy().getFontSet("Substance", null);
                    // Create the wrapper font set
                    FontPolicy newFontPolicy = new FontPolicy() {
                        public FontSet getFontSet(String lafName,
                                              UIDefaults table) {
                            return new WrapperFontSet(substanceCoreFontSet, ss);
                        }
                    };

                    SubstanceLookAndFeel.setFontPolicy(newFontPolicy);
                }
              }
        });

        return new SubstanceOfficeBlack2007LookAndFeel();
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
    public boolean isAvailable() {
        return true;
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
    public <P, C> Resource getOverridesLocation(BeanFactoryLoader.Scope<P, C> scope, P parent, C context) {
        if (scope == BeanFactoryLoader.WINDOW) {
            return new ClassPathResource("/substance-window.xml");
        }
        return null;
    }

    @Override
    public <P, C> void init(BeanFactoryLoader.Scope<P, C> scope, P parent, C context, BeanFactory container) {
    }

}