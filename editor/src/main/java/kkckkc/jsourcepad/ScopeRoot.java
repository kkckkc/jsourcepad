package kkckkc.jsourcepad;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public interface ScopeRoot extends BeanFactoryAware {
	public BeanFactory getBeanFactory();
}
