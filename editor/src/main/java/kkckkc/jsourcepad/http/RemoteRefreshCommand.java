package kkckkc.jsourcepad.http;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.Window;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RemoteRefreshCommand implements RemoteControl.Command {
    public static final String ID = "refresh";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) {
        Window focusedWindow = Application.get().getWindowManager().getFocusedWindow();

        Doc activeDoc = focusedWindow.getDocList().getActiveDoc();
        activeDoc.refresh();

        focusedWindow.topic(Window.FocusListener.class).post().focusGained(focusedWindow);
    }
}
