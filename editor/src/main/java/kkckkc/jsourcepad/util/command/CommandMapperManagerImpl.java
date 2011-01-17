package kkckkc.jsourcepad.util.command;

import com.google.common.collect.Maps;
import kkckkc.jsourcepad.model.Window;
import kkckkc.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Map;

public class CommandMapperManagerImpl implements CommandMapperManager {
    private Window window;

    @Autowired
    public void setWindow(Window window) {
        this.window = window;
    }

    @PostConstruct
    public void init() {
        
    }
    
    @Override
    public Command fromExternalRepresentation(Pair<Class, Map<String, ?>> externalRepresentation) {
        try {
            Class commandClass = externalRepresentation.getFirst();
            Map<String, ?> properties = externalRepresentation.getSecond();

            Command c = (Command) commandClass.newInstance();

            for (String key : properties.keySet()) {
                Field field = findField(commandClass, key);
                if (field == null) {
                    // TODO: Change to logging
                    System.err.println("Unknown field " + key);
                    continue;
                }

                field.setAccessible(true);
                field.set(c, properties.get(key));
            }

            if (c instanceof WindowCommand) {
                ((WindowCommand) c).setWindow(window);
            }

            return c;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Pair<Class, Map<String, ?>> toExternalRepresentation(Command command) {
        return new Pair<Class, Map<String, ?>>(command.getClass(), getPropertyMap(command));
    }

    private Map<String, ?> getPropertyMap(Object command) {
        Map<String, Object> destinationMap = Maps.newHashMap();
        try {
            Class commandClass = command.getClass();
            BeanInfo beanInfo = Introspector.getBeanInfo(commandClass);
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                if ("class".equals(pd.getName())) continue;

                Field field = findField(commandClass, pd.getName());
                if (field == null) {
                    System.err.println("Could not find field " + pd.getName());
                    continue;
                }
                if (field.getAnnotation(CommandProperty.class) != null) {
                    field.setAccessible(true);
                    destinationMap.put(pd.getName(), field.get(command));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return destinationMap;
    }

    private Field findField(Class clazz, String name) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(name)) return field;
        }

        if (Object.class.equals(clazz)) return null;

        return findField(clazz.getSuperclass(), name);
    }

}
