package kkckkc.jsourcepad.theme;

import kkckkc.jsourcepad.model.SettingsManager;
import kkckkc.jsourcepad.model.SettingsPanel;
import kkckkc.jsourcepad.model.ThemeSettings;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import kkckkc.jsourcepad.util.BeanFactoryLoader.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;



public class GtkTheme implements Theme {

	@Override
	public String getLookAndFeel() {
		return "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
	}

    @Override
    public void activate() {
    }

    @Override
    public SettingsPanel getSettingsPanel() {
        return null;
    }

    @Override
    public String getId() {
        return "theme-gtk";
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
    public Resource getOverridesLocation(Scope<?> scope) {
		if (scope == BeanFactoryLoader.DOCUMENT) {
			return new ClassPathResource("/gtk-document.xml");
		} else if (scope == BeanFactoryLoader.WINDOW) {
			return new ClassPathResource("/gtk-window.xml");
		}

		return null;
    }

}
