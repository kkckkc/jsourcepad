package kkckkc.jsourcepad.theme.gtk;

import kkckkc.jsourcepad.ui.IconProvider;

import javax.swing.*;
import java.lang.reflect.Constructor;

public class GtkIconProvider implements IconProvider {
     @Override
    public Icon getIcon(Type type) {
        switch (type) {
            case FILE:
                return getFromStock("gtk-file");
            case FOLDER:
                return getFromStock("gtk-directory");
            default:
                throw new IllegalArgumentException("Unknown icon type " + type);
        }
    }

    private Icon getFromStock(String name) {
        try {
            Class<?> gtkStockIconClass = Class.forName("com.sun.java.swing.plaf.gtk.GTKStyle$GTKStockIcon");
            Constructor<?> constructor = gtkStockIconClass.getDeclaredConstructor(String.class, int.class);
            constructor.setAccessible(true);
            return (Icon) constructor.newInstance(name, 1);
        } catch (Exception ex) {
            return null;
        }
    }

}
