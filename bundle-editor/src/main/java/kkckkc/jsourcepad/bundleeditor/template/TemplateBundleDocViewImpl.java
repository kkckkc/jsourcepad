package kkckkc.jsourcepad.bundleeditor.template;

import kkckkc.jsourcepad.bundleeditor.BasicBundleDocViewImpl;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class TemplateBundleDocViewImpl extends BasicBundleDocViewImpl {

    public static final String OUTPUT_INSERT_AS_TEXT = "Insert as Text";
    private JTextField extension;
    private JComboBox output;

    @Override
    protected void initComponents() {
        super.initComponents();

        extension = new JTextField();
        output = new JComboBox(new String[] { OUTPUT_INSERT_AS_TEXT });
    }

    protected void layout() {
        panel = new JPanel();
        panel.setLayout(new MigLayout("insets panel", "[right][grow,10sp]", "[]r[]r[]r[grow]r[]r[]r[]r[]"));

        layoutHeader();

        panel.add(new JLabel("Extension:"));
        panel.add(extension, "grow,wrap");

        panel.add(new JLabel("Command(s):"), "top");
        panel.add(getSourcePane(), "gapx 3 3,grow,wrap");

        panel.add(new JLabel("Output:"));
        panel.add(output, "wrap");


        layoutFooter();
    }

    public JTextField getExtension() {
        return extension;
    }

    public JComboBox getOutput() {
        return output;
    }
}
