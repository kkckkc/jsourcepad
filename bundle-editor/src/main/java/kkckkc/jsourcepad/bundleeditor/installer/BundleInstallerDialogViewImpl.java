package kkckkc.jsourcepad.bundleeditor.installer;

import kkckkc.jsourcepad.util.ui.BaseJDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class BundleInstallerDialogViewImpl extends BaseJDialog implements BundleInstallerDialogView {
    private JTable table;
    private JButton cancelButton;
    private JButton installButton;
    private JLabel label;
    private JComboBox downloadMethod;

    public BundleInstallerDialogViewImpl() {
        this(null);
    }

    public BundleInstallerDialogViewImpl(java.awt.Frame parent) {
        super(parent, true);
        setModal(true);
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        setTitle("Install Bundles");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        table = new JTable();
        table.setFillsViewportHeight(true);

        installButton = new JButton("Install Selected Bundles");
        cancelButton = new JButton("Cancel");
        label = new JLabel("Available bundles:");

        downloadMethod = new JComboBox(new String[] { "SCM (using git, svn etc.)", "HTTP" });

        Container p = getContentPane();
        p.setLayout(new MigLayout("insets dialog", "[grow]", "[]r[grow]u[]r[]u:push[]"));

        p.add(label, "wrap");

        p.add(new JScrollPane(table), "grow,wrap");

        p.add(new JLabel("Downloading using:"), "wrap");
        p.add(downloadMethod, "wrap");

        p.add(installButton, "split,tag ok");
        p.add(cancelButton, "tag cancel");

        pack();
    }

    @Override
    public JComboBox getDownloadMethod() {
        return downloadMethod;
    }

    @Override
    public JLabel getLabel() {
        return label;
    }

    @Override
    public JTable getTable() {
        return table;
    }

    @Override
    public JButton getCancelButton() {
        return cancelButton;
    }

    @Override
    public JButton getInstallButton() {
        return installButton;
    }

    @Override
    public JDialog getJDialog() {
        return this;
    }

    @Override
    public void setModel(BundleTableModel model) {
        table.setModel(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(1);
        table.getColumnModel().getColumn(0).setCellRenderer(new InstalledFlagCellRenderer());
        table.getColumnModel().getColumn(1).setPreferredWidth(400);
    }


}