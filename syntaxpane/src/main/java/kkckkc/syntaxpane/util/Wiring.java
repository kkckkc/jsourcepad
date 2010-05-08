package kkckkc.syntaxpane.util;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
}
