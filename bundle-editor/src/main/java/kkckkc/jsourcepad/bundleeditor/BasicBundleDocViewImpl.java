package kkckkc.jsourcepad.bundleeditor;

import kkckkc.jsourcepad.ui.DocViewImpl;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class BasicBundleDocViewImpl extends DocViewImpl {

    public static final String
            KEY_EQUIVALENT = "Key Equivalent",
            TAB_TRIGGER = "Tab Trigger";

    protected JPanel panel;
    protected JTextField keyEquivalent;
    protected JTextField tabTrigger;
    protected JTextField scope;
    protected JComboBox activation;
    protected JPanel activationSpec;

    public BasicBundleDocViewImpl() {
        super();

        activation = new JComboBox(new String[] { KEY_EQUIVALENT, TAB_TRIGGER });

        final CardLayout cardLayout = new CardLayout();
        activationSpec = new JPanel();
        activationSpec.setLayout(cardLayout);

        keyEquivalent = new JTextField();
        tabTrigger = new JTextField();

        activationSpec.add(keyEquivalent, KEY_EQUIVALENT);
        activationSpec.add(tabTrigger, TAB_TRIGGER);

        activation.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (itemEvent.getStateChange() != ItemEvent.SELECTED) return;

                cardLayout.show(activationSpec, (String) itemEvent.getItem());
            }
        });

        scope = new JTextField();


        layout();
    }

    protected void layout() {
        panel = new JPanel();
        panel.setLayout(new MigLayout("insets panel", "[right][grow,10sp]", "[]r[]r[grow]r[]r[]r[]r[]"));

        layoutHeader();

        panel.add(new JLabel("Details:"), "top");
        panel.add(getSourcePane(), "gapx 3 3,grow,wrap");

        layoutFooter();
    }

    protected void layoutFooter() {
        panel.add(new JSeparator(JSeparator.HORIZONTAL), "span,growx,wrap");
        panel.add(new JLabel("Activation:"), "");
        panel.add(activation, "split");
        panel.add(activationSpec, "grow, wrap");

        panel.add(new JLabel("Scope Selector:"));
        panel.add(scope, "grow");
    }


    protected void layoutHeader() {
        panel.add(new JLabel("Name:"), "");
        panel.add(new JTextField(), "grow, wrap");
        panel.add(new JSeparator(JSeparator.HORIZONTAL), "span,growx,wrap");
    }

    public JComboBox getActivation() {
        return activation;
    }

    public JTextField getScope() {
        return scope;
    }

    public JTextField getKeyEquivalent() {
        return keyEquivalent;
    }

    public JTextField getTabTrigger() {
        return tabTrigger;
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }
}
