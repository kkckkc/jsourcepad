package kkckkc.jsourcepad.bundleeditor;

import kkckkc.jsourcepad.Plugin;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import org.springframework.core.io.ClassPathResource;
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
    public <P, C> Resource getOverridesLocation(BeanFactoryLoader.Scope<P, C> scope, P parent, C context) {
        if (scope == BeanFactoryLoader.DOCUMENT) {
            Window window = (Window) parent;
            if (window.getProject() != null && window.getProject().getProjectDir().equals(new File(System.getProperty("user.home") + "/.jsourcepad/Shared/Bundles"))) {
                return new ClassPathResource("document-bundle-editor.xml");
            }
        }
        return null;  
    }
}
