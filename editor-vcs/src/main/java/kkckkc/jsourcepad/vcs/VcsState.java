package kkckkc.jsourcepad.vcs;

import java.awt.*;

public enum VcsState {
    DELETED(new Color(97, 97, 97)),
    MODIFIED(new Color(0, 50, 160)),
    ADDED(new Color(10, 119, 0)),
    CONFLICT(new Color(255, 0, 0)),
    IGNORED(new Color(114, 114, 56)),
    UNKNOWN(new Color(153, 51, 0)),
    REPLACED(new Color(10, 119, 0)),
    OBSTRUCTED(new Color(114, 114, 56)),
    EXTERNAL(new Color(114, 160, 56));

    Color color;

    VcsState(Color color) {
        this.color = color;
    }
}
