package kkckkc.jsourcepad.ui.dialog.navigation;

import kkckkc.jsourcepad.util.ui.BaseJDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class QuickNavigationDialogViewImpl extends BaseJDialog implements QuickNavigationDialogView {
    private JTextField textField    ;
    private JList result;
    private JLabel path;

    public QuickNavigationDialogViewImpl(java.awt.Frame parent) {
        super(parent, true);
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        Container p = getContentPane();
        p.setLayout(new MigLayout("insets 0", "[grow,10sp]", "[]r[grow]r[]"));

        textField = new JTextField();
        result = new JList(new String[] { "Lorem", "Ipsum", "Dolor", "Sit", "Amet", "Lorem", "Ipsum", "Dolor", "Sit", "Amet", "Lorem", "Ipsum", "Dolor", "Sit", "Amet"});
        path = new JLabel("Path: ");

        JScrollPane scrollpane = new JScrollPane(result);
        scrollpane.setBorder(null);

        p.add(textField, "wrap,growx,gap 5 5 5 5");
        p.add(scrollpane, "wrap,grow");
        p.add(path, "growx");

        pack();
    }

    @Override
    public JTextField getTextField() {
        return textField;
    }

    @Override
    public JList getResult() {
        return result;
    }

    @Override
    public JDialog getJDialog() {
        return this;
    }

    @Override
    public JLabel getPath() {
        return path;
    }

    public static void main(String... args) {
        QuickNavigationDialogViewImpl q = new QuickNavigationDialogViewImpl(null);
        q.setVisible(true);
    }
}
