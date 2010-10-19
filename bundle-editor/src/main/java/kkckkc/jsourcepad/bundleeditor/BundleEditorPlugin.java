package kkckkc.jsourcepad.bundleeditor;

import kkckkc.jsourcepad.Plugin;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import org.springframework.core.io.Resource;

import java.io.File;

public class BundleEditorPlugin implements Plugin {
    @Override
    public String getId() {
        return "bundle-editor";
    }

    @Override
    public String[] getDependsOn() {
        return new String[] { "editor-ui" };
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public <C> Resource getOverridesLocation(BeanFactoryLoader.Scope<?, C> scope, C context) {
        if (scope == BeanFactoryLoader.WINDOW) {
            if (context != null && ((File) context).equals(new File(System.getProperty("user.home") + "/.jsourcepad/Shared/Bundles"))) {
                System.out.println("BundleEditorPlugin.getOverridesLocation");
            }
        }
        return null;  
    }
}
