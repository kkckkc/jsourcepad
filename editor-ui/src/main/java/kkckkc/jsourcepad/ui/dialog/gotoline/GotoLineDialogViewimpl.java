package kkckkc.jsourcepad.ui.dialog.gotoline;

import kkckkc.jsourcepad.util.ui.BaseJDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class GotoLineDialogViewimpl extends BaseJDialog implements GotoLineDialogView {
    private JTextField lineNumberField;
    private JButton okButton;
    private JLabel lineNumberLabel;

    public GotoLineDialogViewimpl(java.awt.Frame parent) {
        super(parent, true);
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        Container p = getContentPane();
        p.setLayout(new MigLayout("insets dialog", "[grow,10sp]", "[]r[]u:push[]"));

        lineNumberField = new JTextField();
        okButton = new JButton("OK");
        lineNumberLabel = new JLabel("Line number:");

        p.add(lineNumberLabel, "wrap,growx");
        p.add(lineNumberField, "wrap,growx");
        p.add(okButton, "tag ok");

        pack();
    }

    @Override
    public JTextField getLineNumberField() {
        return lineNumberField;
    }

    @Override
    public JButton getOKButton() {
        return okButton;
    }

    @Override
    public JDialog getJDialog() {
        return this;
    }

    public static void main(String... args) {
        GotoLineDialogViewimpl g = new GotoLineDialogViewimpl(null);
        g.setVisible(true);
    }
}
