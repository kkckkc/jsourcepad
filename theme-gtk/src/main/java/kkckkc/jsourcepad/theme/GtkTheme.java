package kkckkc.jsourcepad.theme;

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
    public Resource getOverridesLocation(Scope<?> scope) {
		if (scope == BeanFactoryLoader.DOCUMENT) {
			return new ClassPathResource("/gtk-document.xml");
		} else if (scope == BeanFactoryLoader.WINDOW) {
			return new ClassPathResource("/gtk-window.xml");
		}

		return null;
    }

}
