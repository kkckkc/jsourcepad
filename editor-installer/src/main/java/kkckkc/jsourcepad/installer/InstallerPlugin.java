package kkckkc.jsourcepad.installer;

import kkckkc.jsourcepad.Plugin;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class InstallerPlugin implements Plugin {
    @Override
    public String getId() {
        return "editor-installer";
    }

    @Override
    public String[] getDependsOn() {
        return new String[] { "bundle-editor" };
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public <P, C> Resource getOverridesLocation(BeanFactoryLoader.Scope<P, C> scope, P parent, C context) {
        if (scope == BeanFactoryLoader.APPLICATION) {
            return new ClassPathResource("application-installer.xml");
        }
        return null;
    }

    @Override
    public <P, C> void init(BeanFactoryLoader.Scope<P, C> scope, P parent, C context, BeanFactory container) {
        
    }
}
