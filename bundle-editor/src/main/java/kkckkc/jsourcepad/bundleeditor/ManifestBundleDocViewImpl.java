package kkckkc.jsourcepad.bundleeditor;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class ManifestBundleDocViewImpl extends BasicBundleDocViewImpl {

    public static final String OUTPUT_INSERT_AS_TEXT = "Insert as Text";
    private JTextField extension;
    private JComboBox output;
    private JTree menu;
    private JTree available;

    @Override
    protected void initComponents() {
        super.initComponents();

        extension = new JTextField();
        output = new JComboBox(new String[] { OUTPUT_INSERT_AS_TEXT });
    }

    protected void layout() {
        panel = new JPanel();
        panel.setLayout(new MigLayout("insets panel", "[right][grow,10sp]", "[]r[]r[grow]"));

        layoutHeader();

        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new MigLayout("insets 0", "[grow][grow]", "[grow]"));

        menu = new JTree();
        available = new JTree();

        innerPanel.add(new JScrollPane(menu), "grow");
        innerPanel.add(new JScrollPane(available), "grow");

        panel.add(innerPanel, "grow,span,wrap");
    }

    public JTree getMenu() {
        return menu;
    }

    public JTree getAvailable() {
        return available;
    }

    public JTextField getExtension() {
        return extension;
    }

    public JComboBox getOutput() {
        return output;
    }
}
