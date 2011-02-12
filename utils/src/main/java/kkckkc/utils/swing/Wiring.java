package kkckkc.utils.swing;

import java.awt.*;
import java.beans.*;

public class Wiring {
	public static void wire(final Component source, final Component destination, final String property) {
		wire(source, destination, false, property);
	}
	
	public static void wire(final Component source, final Component destination, boolean setInitial, final String property) {
		final BeanWrapper destBW = new BeanWrapper(destination);

		if (setInitial) {
			BeanWrapper sourceBW = new BeanWrapper(source);
			destBW.set(property, sourceBW.get(property));
		}
		source.addPropertyChangeListener(property, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				destBW.set(property, e.getNewValue());
			}
		});
	}

	public static void wire(Component source, Component destination, String... properties) {
		for (String p : properties) {
			wire(source, destination, p);
		}
	}


    private static class BeanWrapper {
        private Object target;
        private BeanInfo beanInfo;

        public BeanWrapper(Object target) {
            this.target = target;
            try {
                this.beanInfo = Introspector.getBeanInfo(target.getClass());
            } catch (IntrospectionException e) {
                throw new RuntimeException(e);
            }
        }

        public void set(String name, Object value) {
            try {
                for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                    if (pd.getName().equals(name)) {
                        pd.getWriteMethod().invoke(target, value);
                        return;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Object get(String name) {
            try {
                for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                    if (pd.getName().equals(name)) {
                        return pd.getReadMethod().invoke(target);
                    }
                }
                return null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
