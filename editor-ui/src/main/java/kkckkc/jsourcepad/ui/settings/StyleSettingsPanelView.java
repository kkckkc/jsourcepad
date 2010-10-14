package kkckkc.jsourcepad.ui.settings;

import kkckkc.jsourcepad.model.SettingsPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class StyleSettingsPanelView extends JPanel implements SettingsPanel.View {
    public StyleSettingsPanelView() {
        initComponents();
    }

    private void initComponents() {
        setOpaque(false);
        setLayout(new MigLayout("insets panel,fillx", "[right]r[grow]", "[]r[]u[]"));

        add(new JLabel("Font:"), "");
        add(new JTextField(), "split,width 5cm,growx");
        add(new JButton("Select"), "wrap");

        add(new JLabel("Size:"), "");
        add(new JTextField(), "width 2cm,wrap");

        add(new JSeparator(JSeparator.HORIZONTAL), "wrap,span,growx");

        add(new JLabel("Style scheme:"), "");
        add(new JComboBox(), "wrap,growx");
    }

    @Override
    public JPanel getJPanel() {
        return this;  
    }
}
