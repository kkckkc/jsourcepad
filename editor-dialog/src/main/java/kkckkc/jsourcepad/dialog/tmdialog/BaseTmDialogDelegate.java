package kkckkc.jsourcepad.dialog.tmdialog;

import com.google.common.collect.Maps;
import kkckkc.jsourcepad.model.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.CountDownLatch;

public abstract class BaseTmDialogDelegate implements TmDialogDelegate {

    protected Action OK_ACTION = new AbstractAction("Ok") {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
        }
    };
    protected Action CANCEL_ACTION = new AbstractAction("Cancel") {
        @Override
        public void actionPerformed(ActionEvent e) {
            isCancelled = true;
            close();
        }
    };

    private boolean isCancelled = false;
    private boolean center;
    private Window window;

    protected JDialog jdialog;
    protected CountDownLatch latch;

    @Override
    public void open(Window window, boolean center, boolean modal, boolean async) {
        this.center = center;
        this.window = window;

        jdialog = new JDialog(
                modal ? window.getContainer() : null,
                async ? java.awt.Dialog.ModalityType.MODELESS : java.awt.Dialog.ModalityType.DOCUMENT_MODAL);
        if (async) {
            latch = new CountDownLatch(1);
        }

        JPanel panel = new JPanel();
        buildDialog(panel);
        jdialog.setContentPane(panel);
    }

    @Override
    public void show() {
        jdialog.pack();

        if (center) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension d = toolkit.getScreenSize();

            int x = (d.width - jdialog.getSize().width)/2;
            int y = (d.height - jdialog.getSize().height)/2;
            jdialog.setLocation(x, y);

        } else {
            jdialog.setLocationRelativeTo(window.getContainer());
            jdialog.setLocationByPlatform(true);
        }

        jdialog.setVisible(true);
    }

    protected abstract void buildDialog(JPanel panel);

    protected Object getReturnData() {
        return Maps.newHashMap();
    }

    @Override
    public void close() {
        if (latch != null) {
            latch.countDown();
        }
        jdialog.setVisible(false);
    }


    @Override
    public Object waitForClose() {
        if (latch != null) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return isCancelled ? null : getReturnData();
    }

}
