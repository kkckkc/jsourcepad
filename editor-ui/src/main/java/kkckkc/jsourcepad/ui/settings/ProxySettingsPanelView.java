package kkckkc.jsourcepad.ui.settings;

import kkckkc.jsourcepad.model.SettingsPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class ProxySettingsPanelView extends JPanel implements SettingsPanel.View {
    public static final String NO_PROXY = "No proxy", SYSTEM_PROXY = "Use System Proxy Settings",
        MANUAL_PROXY = "Manual Proxy Settings";

    private JTextField host;
    private JTextField port;
    private JButton test;
    private JComboBox type;

    public ProxySettingsPanelView() {
        initComponents();
    }

    private void initComponents() {
        setOpaque(false);
        setLayout(new MigLayout("insets panel,fillx", "[right]r[grow]", "[]r[]r[]u[]"));

        host = new JTextField();
        port = new JTextField();

        type = new JComboBox(new String[] { NO_PROXY, SYSTEM_PROXY, MANUAL_PROXY });

        add(new JLabel("Proxy Type:"), "");
        add(type, "growx,wrap");

        add(new JLabel("Proxy Host:"), "");
        add(host, "width 8cm,growx,wrap");

        add(new JLabel("Proxy Port:"), "");
        add(port, "width 2cm,wrap");

        add(new JSeparator(JSeparator.HORIZONTAL), "wrap,span,growx");

        test = new JButton("Test");
        add(test, "skip");
    }

    @Override
    public JPanel getJPanel() {
        return this;  
    }

    public JTextField getHost() {
        return host;
    }

    public JTextField getPort() {
        return port;
    }

    public JButton getTest() {
        return test;
    }

    public JComboBox getType() {
        return type;
    }
}
