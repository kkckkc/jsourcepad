package kkckkc.jsourcepad.theme.gtk;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.settings.SettingsPanel;
import kkckkc.jsourcepad.model.settings.ThemeSettings;
import kkckkc.jsourcepad.theme.Theme;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import kkckkc.utils.Os;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
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
    public boolean isAvailable() {
        return Os.isLinux();
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
        ThemeSettings ts = Application.get().getSettingsManager().get(ThemeSettings.class);
        return ts.getThemeId() != null && ts.getThemeId().equals(getId());
    }

    @Override
    public <P, C> Resource getOverridesLocation(BeanFactoryLoader.Scope<P, C> scope, P parent, C context, DefaultListableBeanFactory container) {
		if (scope == BeanFactoryLoader.DOCUMENT) {
			return new ClassPathResource("/gtk-document.xml");
		} else if (scope == BeanFactoryLoader.WINDOW) {
			return new ClassPathResource("/gtk-window.xml");
		} else if (scope == BeanFactoryLoader.APPLICATION) {
			return new ClassPathResource("/gtk-application.xml");
		}


		return null;
    }

    @Override
    public <P, C> void init(BeanFactoryLoader.Scope<P, C> scope, P parent, C context, DefaultListableBeanFactory container) {

    }

}
