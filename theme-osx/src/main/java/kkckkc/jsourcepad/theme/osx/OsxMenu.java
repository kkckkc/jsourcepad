package kkckkc.jsourcepad.theme.osx;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.ActionGroup;
import kkckkc.jsourcepad.util.action.ActionManager;
import org.springframework.beans.factory.BeanFactory;

public class OsxMenu {

    public static void init(final BeanFactory container, final Window window) {
        ActionManager am = container.getBean(ActionManager.class);
        ActionGroup ag = am.getActionGroup("file-menu");
        ag.removeAction("file-exit");

        ag = am.getActionGroup("window-menu");
        ag.removeAction("preferences");
    }

}
