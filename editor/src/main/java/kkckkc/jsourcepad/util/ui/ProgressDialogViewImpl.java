package kkckkc.jsourcepad.util.ui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ProgressDialogViewImpl extends BaseJDialog implements ProgressDialogView {
    private JLabel titleLabel;

    public ProgressDialogViewImpl(java.awt.Frame parent) {
        super(parent, ModalityType.DOCUMENT_MODAL);
		setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        Container p = getContentPane();
        p.setLayout(new MigLayout("insets dialog", "[grow,25sp]", "[]r[]u:push[]"));

        titleLabel = new JLabel();

        JProgressBar pb = new JProgressBar();
        pb.setIndeterminate(true);

        JButton cancelButton = new javax.swing.JButton();
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close();
            }
        });

        p.add(titleLabel, "wrap,growx");
        p.add(pb, "wrap,growx");
        p.add(cancelButton, "tag cancel");

        pack();
    }

    @Override
    public JDialog getJDialog() {
        return this;
    }

    @Override
    public void close() {
        setVisible(false);
        dispose();
    }

    @Override
    public void setTitle(String title) {
        titleLabel.setText(title);
        super.setTitle(title);
    }

    public static void main(String... args) {
        ProgressDialogViewImpl progressDialogView = new ProgressDialogViewImpl(null);
        progressDialogView.setTitle("Lorem ipsum dolor sit amet");
        progressDialogView.setVisible(true);
    }
}
