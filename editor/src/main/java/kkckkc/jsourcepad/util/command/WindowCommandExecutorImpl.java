package kkckkc.jsourcepad.util.command;

import kkckkc.jsourcepad.model.Window;
import org.springframework.beans.factory.annotation.Autowired;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

public class WindowCommandExecutorImpl extends AbstractCommandExecutor {
    private Window window;

    @Autowired
    public void setWindow(Window window) {
        this.window = window;
    }

    @Override
    protected void prepareCommand(Command command) {
        if (command instanceof WindowCommand) {
            ((WindowCommand) command).setWindow(window);
        }
    }

    @Override
    protected void doExecute(Command command) {
        command.execute();
    }

}
