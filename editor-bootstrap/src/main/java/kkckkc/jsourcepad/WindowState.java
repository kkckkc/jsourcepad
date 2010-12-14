package kkckkc.jsourcepad;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Doc;
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

            for (Doc doc : window.getDocList().getDocs()) {
                if (! doc.isBackedByFile()) continue;
                wd.openFiles.add(doc.getFile());
            }

            windowState.windows.add(wd);
        }

        Application.get().getPersistenceManager().save(WindowState.class, windowState);
    }

    public static void restore() {
        WindowState windowState = Application.get().getPersistenceManager().load(WindowState.class);
        if (windowState == null) return;

        WindowManager wm = Application.get().getWindowManager();
        for (WindowData wd : windowState.windows) {
            Window window = wm.newWindow(wd.project);

            for (File openFile : wd.openFiles) {
                if (! openFile.exists()) continue;
                window.getDocList().open(openFile);
            }
        }

        // TODO: Handle focused window
    }

    static class WindowData implements Serializable {
        File project;
        List<File> openFiles = new ArrayList<File>();
        boolean focused;
    }

}
