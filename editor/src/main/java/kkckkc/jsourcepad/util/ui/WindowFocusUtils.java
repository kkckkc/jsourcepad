package kkckkc.jsourcepad.util.ui;

import kkckkc.jsourcepad.model.Application;

import javax.swing.*;

public class WindowFocusUtils {
    public static void focusWindow(JFrame frame) {
        if (Application.get().getBrowser().isExternal()) {
            frame.setAlwaysOnTop(true);
            frame.setAlwaysOnTop(false);
        } else {
            frame.requestFocus();
        }
    }

    public static void showAndFocusDialog(JDialog dialog) {
        if (Application.get().getBrowser().isExternal()) dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
        if (Application.get().getBrowser().isExternal()) dialog.setAlwaysOnTop(false);
    }
}
