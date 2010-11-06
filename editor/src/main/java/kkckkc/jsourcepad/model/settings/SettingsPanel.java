package kkckkc.jsourcepad.model.settings;

import javax.swing.*;

public interface SettingsPanel {
    int ORDER_DONT_SHOW = -1;

    public View getView();

    public int getOrder();
    public String getName();

    public void load();
    public boolean save();

    public interface View {
        public JPanel getJPanel();
    }
}
