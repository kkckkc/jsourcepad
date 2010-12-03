package kkckkc.jsourcepad.dialog.tmdialog;

import kkckkc.jsourcepad.model.Window;

import java.util.Map;

public interface TmDialogDelegate {
    public Object execute(Window window, boolean center, boolean modal, boolean async, Map object);

    void close();

    Object waitForClose();
}
