package kkckkc.jsourcepad.bundleeditor.project;

import kkckkc.jsourcepad.action.ActionContextKeys;
import kkckkc.jsourcepad.action.ActionStateRules;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.bundle.BundleStructure;
import kkckkc.jsourcepad.util.action.ActionContext;
import kkckkc.jsourcepad.util.action.ActionStateRule;
import kkckkc.jsourcepad.util.action.BaseAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class NewBundleItemAction extends BaseAction {

    public NewBundleItemAction() {
        putValue(Action.NAME, "New Bundle Item...");
        setActionStateRules(ActionStateRules.FOLDER_SELECTED, new ActionStateRule() {
            @Override
            public boolean shouldBeEnabled(ActionContext actionContext) {
                Object[] tp = actionContext.get(ActionContextKeys.SELECTION);
                return BundleStructure.isBundleItemDir((File) tp[0]) || BundleStructure.isBundleDir(((File) tp[0]).getParentFile());
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Window window = Application.get().getWindowManager().getWindow((JComponent) e.getSource());

        NewBundleItemDialog dialog = new NewBundleItemDialog();
        dialog.setView(new NewBundleItemDialogView(window.getContainer()));
        dialog.setWindow(window);
        dialog.show((File) actionContext.get(ActionContextKeys.SELECTION)[0]);
    }
    
}
