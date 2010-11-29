package kkckkc.jsourcepad;

import kkckkc.jsourcepad.util.BeanFactoryLoader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.Resource;

public interface Plugin {
    public String getId();
    public String[] getDependsOn();
    public boolean isEnabled();

    public <P, C> Resource getOverridesLocation(BeanFactoryLoader.Scope<P, C> scope, P parent, C context, DefaultListableBeanFactory container);

    public <P, C> void init(BeanFactoryLoader.Scope<P, C> scope, P parent, C context, DefaultListableBeanFactory container);
}
