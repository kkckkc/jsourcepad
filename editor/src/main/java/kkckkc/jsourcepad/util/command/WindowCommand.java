package kkckkc.jsourcepad.util.command;

import kkckkc.jsourcepad.model.Window;

public interface WindowCommand extends Command {
    public void setWindow(Window window);
    public Window getWindow();
}
