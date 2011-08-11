package kkckkc.jsourcepad.util.io;

public interface ErrorDialog {
    void show(Throwable details);

    void show(String title, String details);
}
