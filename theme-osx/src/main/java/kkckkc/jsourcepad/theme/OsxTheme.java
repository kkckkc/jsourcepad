package kkckkc.jsourcepad.theme;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import kkckkc.jsourcepad.util.BeanFactoryLoader;
import kkckkc.jsourcepad.util.BeanFactoryLoader.Scope;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;



public class OsxTheme implements Theme {

	public OsxTheme() {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
	}
	
	@Override
	public String getLookAndFeel() {
		return null;
	}

    @Override
    public String getId() {
        return "theme.osx";
    }

    @Override
    public String[] getDependsOn() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        String theme = System.getProperty("theme");
        return getClass().getName().equals(theme);
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
