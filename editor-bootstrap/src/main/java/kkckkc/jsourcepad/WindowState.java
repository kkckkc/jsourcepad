package kkckkc.jsourcepad;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.WindowManager;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WindowState implements Serializable {

    private List<WindowData> windows = new ArrayList<WindowData>();

    public static void save() {
        WindowState windowState = new WindowState();

        WindowManager wm = Application.get().getWindowManager();
        for (Window window : wm.getWindows()) {
            WindowData wd = new WindowData();
            if (window.getProject() != null) {
                wd.project = window.getProject().getProjectDir();
            }
            wd.focused = window == wm.getFocusedWindow();
            windowState.windows.add(wd);

            window.saveState();
        }

        Application.get().getPersistenceManager().save(WindowState.class, windowState);
    }

    public static void restore() {
        WindowState windowState = Application.get().getPersistenceManager().load(WindowState.class);
        if (windowState == null) return;

        Window windowToFocus = null;

        WindowManager wm = Application.get().getWindowManager();
        for (WindowData wd : windowState.windows) {
            Window window = wm.newWindow(wd.project);
            window.restoreState();

            if (wd.focused) windowToFocus = window;
        }

        if (windowToFocus != null) {
            windowToFocus.requestFocus();
        }
    }

    static class WindowData implements Serializable {
        File project;
        boolean focused;
    }

}
