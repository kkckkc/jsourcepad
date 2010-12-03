package kkckkc.jsourcepad.dialog.tmdialog.dialogs;

import com.google.common.collect.Maps;
import kkckkc.jsourcepad.dialog.tmdialog.TmDialogDelegate;
import kkckkc.jsourcepad.model.Window;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class ProgressDialog implements TmDialogDelegate {
    private JDialog jdialog;
    private JProgressBar progress;
    private JLabel summaryLabel;
    private CountDownLatch latch = new CountDownLatch(1);

    @Override
    public Object execute(Window window, boolean center, boolean modal, boolean async, Map object) {
        String title = (String) object.get("title");
        String summary = (String) object.get("summary");
        Boolean progressAnimate = (Boolean) object.get("progressAnimate");

        if (jdialog == null) {
            jdialog = new JDialog(modal ? window.getContainer() : null, async ? java.awt.Dialog.ModalityType.MODELESS : java.awt.Dialog.ModalityType.DOCUMENT_MODAL);
            jdialog.setTitle(title);
            if (center) {
                jdialog.setLocationRelativeTo(null);
            } else {
                jdialog.setLocationRelativeTo(window.getContainer());
                jdialog.setLocationByPlatform(true);
            }

            JButton ok = new JButton("OK");
            ok.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    close();
                }
            });

            JPanel pane = new JPanel();
            pane.setLayout(new MigLayout("insets dialog", "[grow]", "[]r[]u[]"));

            progress = new JProgressBar();
            progress.setIndeterminate(true);
            progress.setEnabled(progressAnimate);

            summaryLabel = new JLabel(summary);

            pane.add(summaryLabel, "wrap");
            pane.add(progress, "wrap");
            pane.add(ok, "split,tag ok");

            jdialog.setContentPane(pane);
            jdialog.pack();
            jdialog.setVisible(true);

        } else {
            if (summary != null)
                summaryLabel.setText(summary);
            if (progressAnimate != null)
                progress.setEnabled(progressAnimate);
        }

        return Maps.newHashMap();
    }

    @Override
    public void close() {
        jdialog.setVisible(false);
        latch.countDown();
    }

    @Override
    public Object waitForClose() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Maps.newHashMap();
    }
}
