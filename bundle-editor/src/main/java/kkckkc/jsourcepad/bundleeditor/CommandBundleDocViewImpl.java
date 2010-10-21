package kkckkc.jsourcepad.bundleeditor;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class CommandBundleDocViewImpl extends BasicBundleDocViewImpl {

    protected void layout() {
        panel = new JPanel();
        panel.setLayout(new MigLayout("insets panel", "[right][grow,10sp]", "[]r[grow]r[]r[]u[]r[]r[]"));

        panel.add(new JLabel("Save:"));
        panel.add(new JComboBox(new String[] { "Nothing", "Current File", "All Files in Project" }), "wrap");

        panel.add(new JLabel("Command(s):"), "top");
        panel.add(getSourcePane(), "grow,wrap");

        panel.add(new JLabel("Input:"));
        panel.add(new JComboBox(new String[] { "Nothing", "Current File", "All Files in Project" }), "wrap");

        panel.add(new JLabel("Output:"));
        panel.add(new JComboBox(new String[] { "Nothing", "Current File", "All Files in Project" }), "wrap");

        layoutFooter();
    }

}
