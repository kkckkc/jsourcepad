package kkckkc.jsourcepad.ui.settings;

import kkckkc.jsourcepad.model.settings.SettingsPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class ThemeSettingsPanelView extends JPanel implements SettingsPanel.View {
    private JComboBox themes;
    private JPanel themePanel;

    public ThemeSettingsPanelView() {
        initComponents();
    }

    private void initComponents() {
        setOpaque(false);
        setLayout(new MigLayout("insets panel,fillx", "[right]r[grow]", "[]u[]"));

        themes = new JComboBox();

        add(new JLabel("Theme:"), "");
        add(themes, "wrap,growx");

        add(new JSeparator(JSeparator.HORIZONTAL), "wrap,span,growx");        

        themePanel = new JPanel();
        themePanel.setOpaque(false);
        add(themePanel, "span");
    }

    @Override
    public JPanel getJPanel() {
        return this;
    }

    public JComboBox getThemes() {
        return themes;
    }

    public JPanel getThemePanel() {
        return themePanel;
    }
}