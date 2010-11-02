package kkckkc.jsourcepad.theme;

import kkckkc.jsourcepad.model.SettingsPanel;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.io.Resource;

import javax.swing.*;


public class DefaultTheme implements Theme {

    @Override
	public LookAndFeel getLookAndFeel() {
		return null;
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
    public <P, C> Resource getOverridesLocation(BeanFactoryLoader.Scope<P, C> scope, P parent, C context) {
	    return null;
    }

    @Override
    public <P, C> void init(BeanFactoryLoader.Scope<P, C> scope, P parent, C context, BeanFactory container) {

    }

}
