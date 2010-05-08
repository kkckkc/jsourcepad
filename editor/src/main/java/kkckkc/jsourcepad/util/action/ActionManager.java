package kkckkc.jsourcepad.util.action;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public class ActionManager implements BeanFactoryAware {

	private BeanFactory beanFactory;

	public ActionGroup getActionGroup(String id) {
		return beanFactory.getBean("action-group-" + id, ActionGroup.class);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

}
