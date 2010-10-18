package kkckkc.jsourcepad.ui;

import kkckkc.jsourcepad.Plugin;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
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
    public <C> Resource getOverridesLocation(BeanFactoryLoader.Scope<?, C> scope, C context) {
        if (scope == BeanFactoryLoader.WINDOW) {
            return new ClassPathResource("/window-ui.xml");
        } else if (scope == BeanFactoryLoader.APPLICATION) {
            return new ClassPathResource("/application-ui.xml");
        } else if (scope == BeanFactoryLoader.DOCUMENT) {
            return new ClassPathResource("/document-ui.xml");
        }
        return null;
    }
}
