package kkckkc.jsourcepad.ui.settings;

import kkckkc.jsourcepad.model.settings.SettingsPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class ProjectSettingsPanelView extends JPanel implements SettingsPanel.View {

    private JTextField excludePattern;

    public ProjectSettingsPanelView() {
        initComponents();
    }

    private void initComponents() {
        setOpaque(false);
        setLayout(new MigLayout("insets panel,fillx", "[right]r[grow]", "[]"));

        excludePattern = new JTextField();

        add(new JLabel("Exluded files:"), "");
        add(excludePattern, "growx,wrap");
    }

    @Override
    public JPanel getJPanel() {
        return this;  
    }

    public JTextField getExcludePattern() {
        return excludePattern;
    }
}
