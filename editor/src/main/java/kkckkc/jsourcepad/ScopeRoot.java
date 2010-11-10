package kkckkc.jsourcepad;

import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public interface ScopeRoot extends BeanFactoryAware {
	public DefaultListableBeanFactory getBeanFactory();
}
