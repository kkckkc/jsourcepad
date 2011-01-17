package kkckkc.jsourcepad.util.command;

import kkckkc.jsourcepad.model.Window;

public abstract class AbstractWindowCommand implements WindowCommand {
    protected Window window;

    @Override
    public void setWindow(Window window) {
        this.window = window;
    }

    @Override
    public Window getWindow() {
        return window;
    }
}
