package kkckkc.jsourcepad.theme;

import kkckkc.jsourcepad.model.SettingsManager;
import kkckkc.jsourcepad.model.SettingsPanel;
import kkckkc.jsourcepad.model.ThemeSettings;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import kkckkc.jsourcepad.util.BeanFactoryLoader.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;



public class OsxTheme implements Theme {

	@Override
	public String getLookAndFeel() {
		return null;
	}

    @Override
    public void activate() {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
    }

    @Override
    public SettingsPanel getSettingsPanel() {
        return null;
    }

    @Override
    public String getId() {
        return "theme-osx";
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
			return new ClassPathResource("/osx-document.xml");
		} else if (scope == BeanFactoryLoader.WINDOW) {
			return new ClassPathResource("/osx-window.xml");
		}
		return null;
    }

}
