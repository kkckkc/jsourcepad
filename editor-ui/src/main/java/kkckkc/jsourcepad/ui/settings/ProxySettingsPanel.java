package kkckkc.jsourcepad.ui.settings;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.settings.ProxySettings;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.model.settings.SettingsPanel;
import kkckkc.jsourcepad.util.Network;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ProxySettingsPanel implements SettingsPanel {
    private ProxySettingsPanelView view;
    private SettingsManager settingsManager;

    public ProxySettingsPanel() {
        this.view = new ProxySettingsPanelView();
        this.settingsManager = Application.get().getSettingsManager();
        this.view.getType().addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED) return;

                view.getHost().setEnabled(e.getItem().equals(ProxySettingsPanelView.MANUAL_PROXY));
                view.getPort().setEnabled(e.getItem().equals(ProxySettingsPanelView.MANUAL_PROXY));
            }
        });

        this.view.getTest().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProxySettings ps = new ProxySettings();
                updateSettings(ps);
                ps.apply();

                if (Network.checkConnectivity()) {
                    JOptionPane.showMessageDialog(view.getJPanel(),
                            "It seems that the Internet is reachable",
                            "Proxy Settings Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                     JOptionPane.showMessageDialog(view.getJPanel(),
                             "It seems that the Internet is not reachable, please verify your settings",
                             "Proxy Settings Problem",
                             JOptionPane.ERROR_MESSAGE);
                }

                // Restore
                Application.get().getSettingsManager().get(ProxySettings.class).apply();
            }
        });
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public int getOrder() {
        return 20;
    }

    @Override
    public String getName() {
        return "Proxy";
    }

    @Override
    public boolean load() {
        ProxySettings proxySettings = settingsManager.get(ProxySettings.class);
        switch (proxySettings.getProxyType()) {
            case NO_PROXY:
                view.getType().setSelectedItem(view.NO_PROXY);
                break;
            case SYSTEM_PROXY:
                view.getType().setSelectedItem(view.SYSTEM_PROXY);
                break;
            case MANUAL_PROXY:
                view.getType().setSelectedItem(view.MANUAL_PROXY);
                view.getHost().setText(proxySettings.getProxyHost());
                view.getPort().setText(proxySettings.getProxyPort());
                break;
        }

        return true;
    }

    @Override
    public boolean save() {
        ProxySettings proxySettings = settingsManager.get(ProxySettings.class);
        updateSettings(proxySettings);
        proxySettings.apply();

        settingsManager.update(proxySettings);

        return false;
    }

    private void updateSettings(ProxySettings proxySettings) {
        if (view.getType().getSelectedItem().equals(view.NO_PROXY)) {
            proxySettings.setProxyType(ProxySettings.ProxyType.NO_PROXY);
            proxySettings.setProxyHost(null);
            proxySettings.setProxyPort(null);
        } else if (view.getType().getSelectedItem().equals(view.SYSTEM_PROXY)) {
            proxySettings.setProxyType(ProxySettings.ProxyType.SYSTEM_PROXY);
            proxySettings.setProxyHost(null);
            proxySettings.setProxyPort(null);
        } else {
            proxySettings.setProxyType(ProxySettings.ProxyType.MANUAL_PROXY);
            proxySettings.setProxyHost(view.getHost().getText());
            proxySettings.setProxyPort(view.getPort().getText());
        }
    }
}
