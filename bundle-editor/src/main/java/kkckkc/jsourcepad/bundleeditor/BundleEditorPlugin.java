package kkckkc.jsourcepad.bundleeditor;

import kkckkc.jsourcepad.Plugin;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.bundle.BundleStructure;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import org.springframework.beans.factory.BeanFactory;
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
            if (window.getProject() != null && BundleStructure.isBundleDir(window.getProject().getProjectDir())) {
                return new ClassPathResource("document-bundle-editor.xml");
            }
        } else if (scope == BeanFactoryLoader.WINDOW) {
            if (BundleStructure.isBundleDir((File) context)) {
                return new ClassPathResource("window-bundle-editor.xml");
            }
        }
        return null;  
    }

    @Override
    public <P, C> void init(BeanFactoryLoader.Scope<P, C> scope, P parent, C context, BeanFactory container) {
        if (scope == BeanFactoryLoader.WINDOW) {
            if (BundleStructure.isBundleDir((File) context)) {
                BundleEditorContextMenu.init(container);
            }
        }
    }
}
