package kkckkc.jsourcepad.theme;

import kkckkc.jsourcepad.Plugin;
import kkckkc.jsourcepad.model.settings.SettingsPanel;


public interface Theme extends Plugin {
	public Object getLookAndFeel();
    public void activate();
    public SettingsPanel getSettingsPanel();
    public boolean isAvailable();
}
