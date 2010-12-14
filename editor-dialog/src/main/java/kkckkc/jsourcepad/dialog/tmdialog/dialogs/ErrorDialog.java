package kkckkc.jsourcepad.dialog.tmdialog.dialogs;

import kkckkc.jsourcepad.dialog.tmdialog.BaseTmDialogDelegate;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ErrorDialog extends BaseTmDialogDelegate {
    private JTextPane message;
    private JPanel buttonPanel;
    private int buttonIndex;

    @Override
    protected void buildDialog(JPanel pane) {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        message = new JTextPane();
        message.setBackground(null);
        message.setEditable(false);
        message.setBorder(null);
        message.setOpaque(false);

        pane.setLayout(new MigLayout("insets dialog", "[grow]", "[grow]u[]"));

        pane.add(message, "wrap");

        pane.add(buttonPanel);
    }

    @Override
    public void load(boolean isFirstTime, Map object) {
        String alertStyle = (String) object.get("alertStyle");
        String messageTitle = (String) object.get("messageTitle");
        String informativeText = (String) object.get("informativeText");
        List<String> buttonTitles = (List<String>) object.get("buttonTitles");
        if (buttonTitles == null || buttonTitles.isEmpty()) {
            buttonTitles = Arrays.asList("Ok");
        }

        int i = 0;
        for (String s : buttonTitles) {
            final int idx = i;

            JButton button = new JButton(s);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    buttonIndex = idx;
                    notifyDataIsAvailable();
                }
            });
            buttonPanel.add(button);
            i++;
        }

        jdialog.setTitle(messageTitle);
        message.setText(informativeText);

        jdialog.pack();
    }

    @Override
    protected Object getReturnData() {
        return buttonIndex;
    }
}
