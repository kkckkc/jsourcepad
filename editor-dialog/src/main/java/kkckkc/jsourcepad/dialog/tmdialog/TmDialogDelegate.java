package kkckkc.jsourcepad.dialog.tmdialog;

import kkckkc.jsourcepad.model.Window;

import java.util.Map;

public interface TmDialogDelegate {
    public void open(Window window, boolean center, boolean modal, boolean async);
    public void load(boolean isFirstTime, Map object);
    public Object waitForClose();
    public void close();
    public void show();
}
