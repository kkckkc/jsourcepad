package kkckkc.jsourcepad.bundleeditor;

import kkckkc.jsourcepad.Plugin;
import kkckkc.jsourcepad.bundleeditor.installer.InstallerMenu;
import kkckkc.jsourcepad.model.Project;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.bundle.BundleStructure;
import kkckkc.jsourcepad.util.BeanFactoryLoader;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
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
            File file = (File) context;
            if (window.getProject() != null && BundleStructure.isOfAnyType(file)) {
                return new ClassPathResource("document-bundle-editor.xml");
            }
        } else if (scope == BeanFactoryLoader.WINDOW) {
            if (BundleStructure.isBundleDir((File) context)) {
                return new ClassPathResource("window-bundle-editor.xml");
            }
        } else if (scope == BeanFactoryLoader.APPLICATION) {
            return new ClassPathResource("application-bundle-editor.xml");
        }
        return null;  
    }

    @Override
    public <P, C> void init(BeanFactoryLoader.Scope<P, C> scope, P parent, C context, BeanFactory container) {
        if (scope == BeanFactoryLoader.WINDOW) {
            Window window = container.getBean(Window.class);
            if (BundleStructure.isBundleDir((File) context)) {
                BundleEditorContextMenu.init(container);
                window.topic(Project.FileChangeListener.class).subscribe(DispatchStrategy.ASYNC, new BundleFileChangeListener());
            }
            InstallerMenu.init(container, window);
        }
    }
}
