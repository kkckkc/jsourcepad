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
    protected void doExecute(Command command) {
        StringBuilder builder = getBeanPropertyValues(command);

        if (command instanceof WindowCommand) {
            ((WindowCommand) command).setWindow(window);
        }
        command.execute();
    }

    private StringBuilder getBeanPropertyValues(Object command) {
        StringBuilder builder = new StringBuilder();
        try {
            Class commandClass = command.getClass();
            BeanInfo beanInfo = Introspector.getBeanInfo(commandClass);
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                if (pd.getName().equals("class")) continue;
                
                Field field = commandClass.getDeclaredField(pd.getName());
                field.setAccessible(true);
                builder.append(pd.getName()).append(" = ").append(field.get(command));
                builder.append(", ");
            }
            if (builder.length() > 0) {
                builder.setLength(builder.length() - 2);
            }
        } catch (Exception e) {
            builder.append("Cannot introspect command");
        }
        return builder;
    }
}
