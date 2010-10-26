package kkckkc.jsourcepad.bundleeditor.project;

import kkckkc.jsourcepad.util.ui.BaseJDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class NewBundleDialogView extends BaseJDialog {
    private JTextField nameField;
    private JButton okButton;
    private JButton cancelButton;

    public NewBundleDialogView(Frame parent) {
        super(parent, true);
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        setTitle("New Bundle");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        Container p = getContentPane();
        p.setLayout(new MigLayout("insets dialog", "[align right][grow,10sp]", "[]u:push[]"));

        nameField = new JTextField();
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        p.add(new JLabel("Name:"), "");
        p.add(nameField, "wrap,growx");


        p.add(okButton, "tag ok,span,split");
        p.add(cancelButton, "tag cancel");

        pack();
    }

    public JTextField getNameField() {
        return nameField;
    }

    public JButton getOKButton() {
        return okButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JDialog getJDialog() {
        return this;
    }

    public static void main(String... args) {
        NewBundleDialogView g = new NewBundleDialogView(null);
        g.setVisible(true);
    }
}
