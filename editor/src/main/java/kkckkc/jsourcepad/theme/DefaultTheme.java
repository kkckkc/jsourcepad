package kkckkc.jsourcepad.theme;

import kkckkc.jsourcepad.util.BeanFactoryLoader.Scope;

import org.springframework.core.io.Resource;



public class DefaultTheme implements Theme {

	@Override
	public String getLookAndFeel() {
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
    public Resource getOverridesLocation(Scope<?> scope) {
	    return null;
    }

}
