package kkckkc.jsourcepad.dialog.tmdialog.dialogs;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import kkckkc.jsourcepad.dialog.tmdialog.BaseTmDialogDelegate;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class RequestStringDialog extends BaseTmDialogDelegate {
    private JTextPane message;
    private JButton button1;
    private JButton button2;
    private JTextField textField;
    private int button = -1;

    @Override
    protected void buildDialog(JPanel pane) {
        button1 = new JButton("Ok");
        button2 = new JButton("Cancel");

        textField = new JTextField();

        message = new JTextPane();
        message.setBackground(null);
        message.setEditable(false);
        message.setBorder(null);
        message.setOpaque(false);

        pane.setLayout(new MigLayout("insets dialog", "[grow]", "[grow]r[]u[]"));

        pane.add(message, "wrap");

        pane.add(textField, "growx,wrap");

        pane.add(button1, "split,tag ok");
        pane.add(button2, "tag cancel,wrap");

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button = 1;
                notifyDataIsAvailable();
            }
        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button = 2;
                notifyDataIsAvailable();
            }
        });
    }

    @Override
    public void load(boolean isFirstTime, Map object) {
        String title = (String) object.get("title");
        String prompt = (String) object.get("prompt");
        String string = (String) object.get("string");
        String button1Label = (String) object.get("button1");
        String button2Label = (String) object.get("button2");

        textField.setText(Objects.firstNonNull(string, ""));
        if (! Strings.isNullOrEmpty(button1Label)) button1.setText(button1Label);
        if (! Strings.isNullOrEmpty(button2Label)) button2.setText(button2Label);

        jdialog.setTitle(title);
        message.setText(prompt);

        textField.requestFocusInWindow();

        jdialog.pack();
    }

    @Override
    protected Object getReturnData() {
        Map m = Maps.newHashMap();
        if (button == 1) {
            Map result = Maps.newHashMap();
            m.put("result", result);
            result.put("returnArgument", textField.getText());
        }
        return m;
    }
}
