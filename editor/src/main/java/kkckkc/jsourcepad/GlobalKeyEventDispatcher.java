package kkckkc.jsourcepad;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.bundle.BundleItemSupplier;
import kkckkc.jsourcepad.model.bundle.BundleManager;
import kkckkc.jsourcepad.model.bundle.BundleMenuProvider;
import kkckkc.jsourcepad.util.action.ActionGroup;
import kkckkc.jsourcepad.util.action.DelegatingAction;
import kkckkc.jsourcepad.util.action.MenuFactory;
import kkckkc.syntaxpane.model.Scope;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;

public class GlobalKeyEventDispatcher implements KeyEventDispatcher {

    private final BundleManager bundleManager;

    public GlobalKeyEventDispatcher(BundleManager bundleManager) {
        this.bundleManager = bundleManager;
    }

    public boolean dispatchKeyEvent(final KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_RELEASED) {
            return false;
        }
        if (e.getKeyCode() == KeyEvent.VK_CONTROL || e.getKeyCode() == KeyEvent.VK_SHIFT || e.getKeyCode() == KeyEvent.VK_META || e.getKeyCode() == KeyEvent.VK_META || e.getKeyCode() == KeyEvent.VK_ALT) {
            return false;
        }
        if (e.getID() == KeyEvent.KEY_TYPED) {
            return false;
        }
        if ((Character.isLetter(e.getKeyChar()) || Character.isDigit(e.getKeyChar())) && e.getModifiers() <= 1) {
            return false;
        }
        Window window = Application.get().getWindowManager().getWindow((JComponent) e.getComponent());
        if (window == null) {
            return false;
        }
        if (window.getDocList().getActiveDoc() == null) {
            return false;
        }

        Scope scope = window.getDocList().getActiveDoc().getActiveBuffer().getInsertionPoint().getScope();
        Collection<BundleItemSupplier> items = bundleManager.getItemsForShortcut(e, scope);

        if (!items.isEmpty()) {
            final ActionGroup tempActionGroup = new ActionGroup();

            int i = 1;
            for (BundleItemSupplier r : items) {
                if (r == null) {
                    tempActionGroup.add(null);
                } else {
                    if (i < 10) {
                        tempActionGroup.add(new DelegatingAction(BundleMenuProvider.getActionForItem(r.getUUID()),
                                KeyStroke.getKeyStroke(Integer.toString(i).charAt(0))));
                    } else {
                        tempActionGroup.add(BundleMenuProvider.getActionForItem(r.getUUID()));
                    }
                    i++;
                }
            }
            if (tempActionGroup.size() > 1) {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        JPopupMenu jpm = new MenuFactory().buildPopup(tempActionGroup, null);
                        Point point = MouseInfo.getPointerInfo().getLocation();
                        Point componentPosition = e.getComponent().getLocationOnScreen();
                        point.translate(-(int) componentPosition.getX(), -(int) componentPosition.getY());
                        jpm.show(e.getComponent(), point.x, point.y);
                    }
                });
            } else {
                tempActionGroup.getItems().get(0).actionPerformed(new ActionEvent(e.getComponent(), 1, null));
            }
            e.consume();
            return true;
        }
        return false;
    }
}
