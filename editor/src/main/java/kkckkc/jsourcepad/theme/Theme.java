package kkckkc.jsourcepad.theme;

import kkckkc.jsourcepad.util.BeanFactoryLoader;

import org.springframework.core.io.Resource;



public interface Theme {
	public String getLookAndFeel();
	public Resource getOverridesLocation(BeanFactoryLoader.Scope<?> scope);
}
