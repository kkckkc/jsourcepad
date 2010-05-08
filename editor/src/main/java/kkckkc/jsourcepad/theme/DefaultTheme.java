package kkckkc.jsourcepad.theme;

import kkckkc.jsourcepad.util.BeanFactoryLoader.Scope;

import org.springframework.core.io.Resource;



public class DefaultTheme implements Theme {

	@Override
	public String getLookAndFeel() {
		return null;
	}

	@Override
    public Resource getOverridesLocation(Scope<?> scope) {
	    return null;
    }

}
