package kkckkc.jsourcepad.theme;

import kkckkc.jsourcepad.model.SettingsPanel;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
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
    public String getId() {
        return "theme.default";
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
    public <C> Resource getOverridesLocation(BeanFactoryLoader.Scope<?, C> scope, C context) {
	    return null;
    }

}
