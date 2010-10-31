package kkckkc.jsourcepad.ui.dialog.error;

import kkckkc.jsourcepad.util.ui.BaseJDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ErrorDialogViewImpl extends BaseJDialog implements ErrorDialogView {
    private JLabel titleLabel;
    private JTextArea details;

    public ErrorDialogViewImpl() {
        super(null, ModalityType.APPLICATION_MODAL);
		setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        Container p = getContentPane();
        p.setLayout(new MigLayout("insets dialog", "[grow,16cm]", "[]r[grow,10cm]u[]"));

        titleLabel = new JLabel();

        details = new JTextArea();
        details.setFont(Font.decode(Font.MONOSPACED));

        JButton okButton = new JButton();
        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close();
            }
        });

        p.add(titleLabel, "wrap,growx");
        p.add(new JScrollPane(details, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), "wrap,grow");
        p.add(okButton, "tag cancel");

        pack();
    }

    @Override
    public JTextArea getDetails() {
        return details;
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
        titleLabel.setText(title.length() > 40 ? title.substring(0, 39) + "..." : title);
        super.setTitle(title);
    }

    public static void main(String... args) {
        ErrorDialogViewImpl progressDialogView = new ErrorDialogViewImpl();
        progressDialogView.setTitle("Lorem ipsum dolor sit amet");
        progressDialogView.setVisible(true);
    }
}
