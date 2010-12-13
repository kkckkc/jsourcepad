package kkckkc.jsourcepad.bundleeditor;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Project;
import kkckkc.jsourcepad.model.bundle.Bundle;
import kkckkc.jsourcepad.model.bundle.BundleManager;
import kkckkc.jsourcepad.util.Config;
import kkckkc.utils.io.FileUtils;

import java.io.File;

public class BundleFileChangeListener implements Project.RefreshListener {

    @Override
    public void refreshed(File file) {
        BundleManager bm = Application.get().getBundleManager();
        if (FileUtils.isAncestorOf(file, Config.getBundlesFolder())) {
            if (! file.exists()) {
                if (file.getParentFile().equals(Config.getBundlesFolder())) {
                    Bundle bundle = bm.getBundle(file);
                    bm.remove(bundle);
                } else {
                    // Bundle added
                }
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
}
