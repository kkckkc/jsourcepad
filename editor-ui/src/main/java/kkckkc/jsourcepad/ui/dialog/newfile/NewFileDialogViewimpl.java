package kkckkc.jsourcepad.ui.dialog.newfile;

import kkckkc.jsourcepad.util.ui.BaseJDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class NewFileDialogViewimpl extends BaseJDialog implements NewFileDialogView {
    private JTextField fileNameField;
    private JButton okButton;
    private JButton cancelButton;
    private JTextField locationField;
    private JComboBox templateField;

    public NewFileDialogViewimpl(Frame parent) {
        super(parent, true);
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        Container p = getContentPane();
        p.setLayout(new MigLayout("insets dialog", "[align right][grow,10sp]", "[]r[]r[]u:push[]"));

        fileNameField = new JTextField();
        locationField = new JTextField();
        templateField = new JComboBox();
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        p.add(new JLabel("File Name:"), "");
        p.add(fileNameField, "wrap,growx");

        p.add(new JLabel("Location:"), "");
        p.add(locationField, "wrap,growx");

        p.add(new JLabel("Template:"), "");
        p.add(templateField, "wrap,growx");


        p.add(okButton, "tag ok,span,split");
        p.add(cancelButton, "tag cancel");

        pack();
    }

    @Override
    public JTextField getFileNameField() {
        return fileNameField;
    }

    @Override
    public JButton getOKButton() {
        return okButton;
    }

    @Override
    public JButton getCancelButton() {
        return cancelButton;
    }

    @Override
    public JTextField getLocationField() {
        return locationField;
    }

    @Override
    public JComboBox getTemplateField() {
        return templateField;
    }

    @Override
    public JDialog getJDialog() {
        return this;
    }

    public static void main(String... args) {
        NewFileDialogViewimpl g = new NewFileDialogViewimpl(null);
        g.setVisible(true);
    }
}
