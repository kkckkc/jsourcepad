package kkckkc.jsourcepad.os.windows;

import kkckkc.jsourcepad.Plugin;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import kkckkc.utils.Os;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class WindowsPlugin implements Plugin {
    @Override
    public String getId() {
        return "os-windows";
    }

    @Override
    public String[] getDependsOn() {
        return new String[] { "editor-ui" };
    }

    @Override
    public boolean isEnabled() {
        return Os.isWindows();
    }

    @Override
    public <P, C> Resource getOverridesLocation(BeanFactoryLoader.Scope<P, C> scope, P parent, C context) {
        if (scope == BeanFactoryLoader.APPLICATION) {
            return new ClassPathResource("/application-os-windows.xml");
        }
        return null;
    }

    @Override
    public <P, C> void init(BeanFactoryLoader.Scope<P, C> scope, P parent, C context, BeanFactory container) {
    }
}
