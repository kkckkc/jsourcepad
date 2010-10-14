package kkckkc.jsourcepad.ui.settings;

import kkckkc.jsourcepad.model.SettingsPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class ThemeSettingsPanelView extends JPanel implements SettingsPanel.View {
    public ThemeSettingsPanelView() {
        initComponents();
    }

    private void initComponents() {
        setOpaque(false);
        setLayout(new MigLayout("insets panel,fillx", "[right]r[grow]", "[]u[]"));

        add(new JLabel("Theme:"), "");
        add(new JComboBox(), "wrap,growx");

        add(new JSeparator(JSeparator.HORIZONTAL), "wrap,span,growx");        

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        add(panel, "split");
    }

    @Override
    public JPanel getJPanel() {
        return this;
    }
}