package kkckkc.jsourcepad.theme.osx;

import kkckkc.jsourcepad.util.action.ActionGroup;
import kkckkc.jsourcepad.util.action.ActionManager;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import javax.annotation.PostConstruct;

public class OsxMenu implements BeanFactoryAware {


    private BeanFactory beanFactory;

    @PostConstruct
    public void init() {
        ActionManager am = beanFactory.getBean(ActionManager.class);
        ActionGroup ag = am.getActionGroup("file-menu");
        ag.remove(ag.size() - 1);
        ag.remove(ag.size() - 1);

    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
