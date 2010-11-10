package kkckkc.jsourcepad.bundleeditor.installer;

import kkckkc.jsourcepad.View;

import javax.swing.*;

public interface BundleInstallerDialogView extends View {
    JTable getTable();

    JButton getCancelButton();

    JButton getInstallButton();

    JDialog getJDialog();

    void setModel(BundleTableModel model);

    JLabel getLabel();

    JComboBox getDownloadMethod();
}
