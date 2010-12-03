package kkckkc.jsourcepad.dialog.tmdialog.dialogs;

import com.google.common.collect.Maps;
import kkckkc.jsourcepad.dialog.tmdialog.TmDialogDelegate;
import kkckkc.jsourcepad.model.Window;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class SimpleNotificationWindow implements TmDialogDelegate {
    @Override
    public Object execute(Window window, boolean center, boolean modal, boolean async, Map object) {
        String title = (String) object.get("title");
        String summary = (String) object.get("summary");
        String log = (String) object.get("log");

        final JDialog jdialog = new JDialog(modal ? window.getContainer() : null, java.awt.Dialog.ModalityType.DOCUMENT_MODAL);
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
                jdialog.setVisible(false);
            }
        });

        JTextArea textArea = new JTextArea(10, 50);
        textArea.setText(log);
        textArea.setEditable(false);

        JPanel pane = new JPanel();
        pane.setLayout(new MigLayout("insets dialog", "[grow]", "[]r[grow]u[]"));

        pane.add(new JLabel(summary), "wrap");
        pane.add(new JScrollPane(textArea), "wrap");
        pane.add(ok, "split,tag ok");

        jdialog.setContentPane(pane);
        jdialog.pack();
        jdialog.setVisible(true);

        return Maps.newHashMap();
    }

    @Override
    public void close() {
    }

    @Override
    public Object waitForClose() {
        return null;
    }
}
