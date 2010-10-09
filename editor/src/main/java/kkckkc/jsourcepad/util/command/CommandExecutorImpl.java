package kkckkc.jsourcepad.util.command;

import kkckkc.jsourcepad.model.Window;
import org.springframework.beans.factory.annotation.Autowired;

public class CommandExecutorImpl implements CommandExecutor {
    private Window window;

    @Autowired
    public void setWindow(Window window) {
        this.window = window;
    }

    @Override
    public void execute(Command command) {
        command.execute(window);
    }
}
