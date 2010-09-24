
package kkckkc.jsourcepad.util.action;

import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

public class DefaultAcceleratorManager implements AcceleratorManager {

    private Properties props;

    @Autowired
	public void setProperties(Properties props) {
		this.props = props;
	}

    @Override
    public KeyStroke getKeyStroke(String action) {
        String value = props.getProperty(action + ".Accelerator");
        if (value == null) return null;
        return parseKeyStroke(value);
    }

    public KeyStroke parseKeyStroke(String value) {
		if (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() == Event.CTRL_MASK) {
			value = value.replaceAll("valt", "alt");
			value = value.replaceAll("vctrl", "meta");
			value = value.replaceAll("vcmd", "ctrl");
		} else if (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() == Event.META_MASK) {
			value = value.replaceAll("valt", "alt");
			value = value.replaceAll("vctrl", "ctrl");
			value = value.replaceAll("vcmd", "meta");
		} else {
			value = value.replaceAll("valt", "meta");
			value = value.replaceAll("vctrl", "ctrl");
			value = value.replaceAll("vcmd", "alt");
		}

		return KeyStroke.getKeyStroke(value);
    }

}
