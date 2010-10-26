package kkckkc.jsourcepad.bundleeditor.project;

import kkckkc.jsourcepad.action.ActionContextKeys;
import kkckkc.jsourcepad.action.ActionStateRules;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.ActionContext;
import kkckkc.jsourcepad.util.action.ActionStateRule;
import kkckkc.jsourcepad.util.action.BaseAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class NewBundleAction extends BaseAction {

    public NewBundleAction() {
        putValue(Action.NAME, "New Bundle...");
        setActionStateRules(ActionStateRules.FOLDER_SELECTED, new ActionStateRule() {
            @Override
            public boolean shouldBeEnabled(ActionContext actionContext) {
                Object[] tp = actionContext.get(ActionContextKeys.SELECTION);
                return ((File) tp[0]).equals(Application.get().getBundleManager().getBundleDir());
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Window window = Application.get().getWindowManager().getWindow((JComponent) e.getSource());

        NewBundleDialog dialog = new NewBundleDialog();
        dialog.setView(new NewBundleDialogView(window.getContainer()));
        dialog.setWindow(window);
        dialog.show();
    }
    
}
