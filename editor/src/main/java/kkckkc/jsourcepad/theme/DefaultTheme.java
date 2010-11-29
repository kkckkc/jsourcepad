package kkckkc.jsourcepad.theme;

import kkckkc.jsourcepad.model.settings.SettingsPanel;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.Resource;

import javax.swing.*;


public class DefaultTheme implements Theme {

    @Override
	public Object getLookAndFeel() {
		return UIManager.getSystemLookAndFeelClassName();
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
        return true;
    }

    @Override
    public String getId() {
        return "theme-default";
    }

    @Override
    public String[] getDependsOn() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public <P, C> Resource getOverridesLocation(BeanFactoryLoader.Scope<P, C> scope, P parent, C context, DefaultListableBeanFactory container) {
	    return null;
    }

    @Override
    public <P, C> void init(BeanFactoryLoader.Scope<P, C> scope, P parent, C context, DefaultListableBeanFactory container) {

    }

}
