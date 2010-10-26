package kkckkc.jsourcepad.bundleeditor.project;

import kkckkc.jsourcepad.util.ui.BaseJDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class NewBundleItemDialogView extends BaseJDialog {
    private JTextField nameField;
    private JButton okButton;
    private JButton cancelButton;
    private JComboBox typeField;

    public NewBundleItemDialogView(Frame parent) {
        super(parent, true);
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        setTitle("New Bundle Item");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        Container p = getContentPane();
        p.setLayout(new MigLayout("insets dialog", "[align right][grow,10sp]", "[]r[]u:push[]"));

        nameField = new JTextField();
        typeField = new JComboBox();
        typeField.addItem("Command");
        typeField.addItem("Preference");
        typeField.addItem("Snippet");
        typeField.addItem("Syntax");
        typeField.addItem("Template");

        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        p.add(new JLabel("Name:"), "");
        p.add(nameField, "wrap,growx");


        p.add(new JLabel("Type:"), "");
        p.add(typeField, "wrap,growx");


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

    public JComboBox getTypeField() {
        return typeField;
    }

    public static void main(String... args) {
        NewBundleItemDialogView g = new NewBundleItemDialogView(null);
        g.setVisible(true);
    }
}
