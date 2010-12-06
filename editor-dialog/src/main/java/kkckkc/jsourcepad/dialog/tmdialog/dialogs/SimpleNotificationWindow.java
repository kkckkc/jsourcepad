package kkckkc.jsourcepad.dialog.tmdialog.dialogs;

import kkckkc.jsourcepad.dialog.tmdialog.BaseTmDialogDelegate;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Map;

public class SimpleNotificationWindow extends BaseTmDialogDelegate {
    private JLabel summaryLabel;
    private JTextArea textArea;

    @Override
    protected void buildDialog(JPanel pane) {
        JButton ok = new JButton(OK_ACTION);

        textArea = new JTextArea(10, 50);
        textArea.setText("");
        textArea.setEditable(false);

        pane.setLayout(new MigLayout("insets dialog", "[grow]", "[]r[grow]u[]"));

        summaryLabel = new JLabel("");
        pane.add(summaryLabel, "wrap");
        pane.add(new JScrollPane(textArea), "wrap");
        pane.add(ok, "split,tag ok");
    }

    @Override
    public void load(boolean isFirstTime, Map object) {
        String title = (String) object.get("title");
        String summary = (String) object.get("summary");
        String log = (String) object.get("log");

        jdialog.setTitle(title);
        summaryLabel.setText(summary);
        textArea.setText(log);
    }
}
