package kkckkc.jsourcepad.bundleeditor;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Project;
import kkckkc.jsourcepad.model.bundle.Bundle;
import kkckkc.jsourcepad.model.bundle.BundleManager;
import kkckkc.utils.io.FileUtils;

import java.io.File;

public class BundleFileChangeListener implements Project.FileChangeListener {
    @Override
    public void removed(File file) {
        BundleManager bm = Application.get().getBundleManager();
        if (FileUtils.isAncestorOf(file, bm.getBundleDir())) {
            // Did we delete a bundle
            if (file.getParentFile().equals(bm.getBundleDir())) {
                Bundle bundle = bm.getBundle(file);
                bm.remove(bundle);

            } else {
                // Find bundle
                while (! file.getParentFile().equals(bm.getBundleDir())) {
                    file = file.getParentFile();
                }

                // Reload
                Bundle bundle = bm.getBundle(file);
                bm.reload(bundle);
            }
        }
    }

    @Override
    public void created(File file) {
    }
    
}
