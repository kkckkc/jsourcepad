package kkckkc.jsourcepad.ui;

import kkckkc.jsourcepad.Plugin;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class UIPlugin implements Plugin {
    @Override
    public String getId() {
        return "editor-ui";
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
        if (scope == BeanFactoryLoader.WINDOW) {
            return new ClassPathResource("/window-ui.xml");
        } else if (scope == BeanFactoryLoader.APPLICATION) {
            return new ClassPathResource("/application-ui.xml");
        } else if (scope == BeanFactoryLoader.DOCUMENT) {
            return new ClassPathResource("/document-ui.xml");
        }
        return null;
    }

    @Override
    public <P, C> void init(BeanFactoryLoader.Scope<P, C> scope, P parent, C context, DefaultListableBeanFactory container) {
    }
}
