package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.dialog.navigation.BundleItemNavigationDialog;
import kkckkc.jsourcepad.util.action.BaseAction;

import java.awt.event.ActionEvent;

public class BundlesSelectBundleItemAction extends BaseAction {

    private Window window;

    public BundlesSelectBundleItemAction(Window w) {
        this.window = w;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BundleItemNavigationDialog dialog = window.getPresenter(BundleItemNavigationDialog.class);
        dialog.show();
    }


}
