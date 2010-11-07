package kkckkc.jsourcepad.model;

public class WindowLocator {

    private static Window window;

    public static Window get() {
        return window;
    }

    public static void set(Window w) {
        window = w;
    }

}
