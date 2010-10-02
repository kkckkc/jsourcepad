package kkckkc.jsourcepad.util.command;

import kkckkc.jsourcepad.model.Window;

import java.io.Serializable;

public interface Command extends Serializable {
    public void execute(Window window);
}
