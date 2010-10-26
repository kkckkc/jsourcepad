package kkckkc.jsourcepad.bundleeditor;

import kkckkc.jsourcepad.bundleeditor.project.NewBundleAction;
import kkckkc.jsourcepad.bundleeditor.project.NewBundleItemAction;
import kkckkc.jsourcepad.util.action.ActionGroup;
import kkckkc.jsourcepad.util.action.ActionManager;
import org.springframework.beans.factory.BeanFactory;

public class BundleEditorContextMenu {

    public static void init(BeanFactory beanFactory) {
        ActionManager am = beanFactory.getBean(ActionManager.class);
        ActionGroup ag = am.getActionGroup("project-context-menu");
        ag.add(0, new NewBundleAction());
        ag.add(1, new NewBundleItemAction());

        ag.add(2, null);
    }

}
