package kkckkc.jsourcepad.ui;

import javax.swing.*;

public interface IconProvider {

    public static enum Type { FOLDER, FILE };

    public Icon getIcon(Type type);

}
