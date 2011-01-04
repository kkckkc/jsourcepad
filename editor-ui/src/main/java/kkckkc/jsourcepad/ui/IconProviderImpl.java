package kkckkc.jsourcepad.ui;

import javax.swing.*;

public class IconProviderImpl implements IconProvider {
    @Override
    public Icon getIcon(Type type) {
        switch (type) {
            case FILE:
                return UIManager.getDefaults().getIcon("FileView.fileIcon");
            case FOLDER:
                return UIManager.getDefaults().getIcon("FileChooser.newFolderIcon");
            default:
                throw new IllegalArgumentException("Unknown icon type " + type);
        }
    }
}
