package kkckkc.jsourcepad.bundleeditor;

import kkckkc.jsourcepad.util.action.ActionGroup;
import kkckkc.jsourcepad.util.action.ActionManager;
import org.springframework.beans.factory.BeanFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class BundleEditorContextMenu {

    public static void init(BeanFactory beanFactory) {
        ActionManager am = beanFactory.getBean(ActionManager.class);
        ActionGroup ag = am.getActionGroup("project-context-menu");
        ag.add(0, new AbstractAction("New Bundle...") {

            @Override
            public void actionPerformed(ActionEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        ag.add(1, new AbstractAction("New Item...") {

            @Override
            public void actionPerformed(ActionEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        ag.add(2, null);
    }

}
