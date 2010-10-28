package kkckkc.jsourcepad.util.io;

import java.awt.*;

public interface ErrorDialog {
    void show(Throwable details, Container parent);

    void show(String title, String details, Container parent);
}
