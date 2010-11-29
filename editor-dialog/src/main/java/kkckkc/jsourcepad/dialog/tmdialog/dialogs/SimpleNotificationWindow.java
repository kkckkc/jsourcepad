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
    public Object execute(Window window, Map object) {
        String title = (String) object.get("title");
        String summary = (String) object.get("summary");
        String log = (String) object.get("log");

        final JDialog jdialog = new JDialog(window.getContainer(), java.awt.Dialog.ModalityType.DOCUMENT_MODAL);
        jdialog.setTitle(title);
        jdialog.setLocationRelativeTo(window.getContainer());
        jdialog.setLocationByPlatform(true);

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
}
