package kkckkc.syntaxpane.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

public class BeanWrapper {
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
