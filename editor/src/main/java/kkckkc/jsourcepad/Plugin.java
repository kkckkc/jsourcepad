package kkckkc.jsourcepad;

import kkckkc.jsourcepad.util.BeanFactoryLoader;
import org.springframework.core.io.Resource;

public interface Plugin {
    public String getId();
    public String[] getDependsOn();
    public boolean isEnabled();

    public Resource getOverridesLocation(BeanFactoryLoader.Scope<?> scope);
}
