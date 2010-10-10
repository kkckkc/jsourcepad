package kkckkc.jsourcepad.util.command;

import kkckkc.jsourcepad.model.Window;
import org.springframework.beans.factory.annotation.Autowired;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

public class CommandExecutorImpl implements CommandExecutor {
    private Window window;

    @Autowired
    public void setWindow(Window window) {
        this.window = window;
    }

    @Override
    public void execute(Command command) {
        StringBuilder b = getBeanPropertyValues(command);
        System.out.println("Executing: " + command.getClass().getSimpleName() + "[" + b.toString() + "]");

        command.execute(window);
    }

    private StringBuilder getBeanPropertyValues(Command command) {
        StringBuilder b = new StringBuilder();
        try {
            Class c = command.getClass();
            BeanInfo beanInfo = Introspector.getBeanInfo(c);
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                if (pd.getName().equals("class")) continue;
                
                Field f = c.getDeclaredField(pd.getName());
                f.setAccessible(true);
                b.append(pd.getName() + " = " + f.get(command));
                b.append(", ");
            }
            if (b.length() > 0) {
                b.setLength(b.length() - 2);
            }
        } catch (Exception e) {
            b.append("Cannot introspect command");
        }
        return b;
    }
}
