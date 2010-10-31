package kkckkc.jsourcepad.bundleeditor;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Project;
import kkckkc.jsourcepad.model.bundle.Bundle;
import kkckkc.jsourcepad.model.bundle.BundleManager;
import kkckkc.jsourcepad.util.Config;
import kkckkc.utils.io.FileUtils;

import java.io.File;

public class BundleFileChangeListener implements Project.FileChangeListener {
    @Override
    public void renamed(File newFile, File oldFile) {
        removed(oldFile);

        BundleManager bm = Application.get().getBundleManager();
        if (FileUtils.isAncestorOf(newFile, Config.getBundlesFolder())) {
            // Did we delete a bundle
            if (newFile.getParentFile().equals(Config.getBundlesFolder())) {
                bm.addBundle(newFile);

            } else {
                // Find bundle
                while (! newFile.getParentFile().equals(Config.getBundlesFolder())) {
                    newFile = newFile.getParentFile();
                }

                // Reload
                Bundle bundle = bm.getBundle(newFile);
                bm.reload(bundle);
            }
        }

    }

    @Override
    public void removed(File file) {
        BundleManager bm = Application.get().getBundleManager();
        if (FileUtils.isAncestorOf(file, Config.getBundlesFolder())) {
            // Did we delete a bundle
            if (file.getParentFile().equals(Config.getBundlesFolder())) {
                Bundle bundle = bm.getBundle(file);
                bm.remove(bundle);

            } else {
                // Find bundle
                while (! file.getParentFile().equals(Config.getBundlesFolder())) {
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
