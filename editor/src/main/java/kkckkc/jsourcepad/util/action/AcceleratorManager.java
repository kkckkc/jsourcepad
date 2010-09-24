
package kkckkc.jsourcepad.util.action;

import javax.swing.*;

public interface AcceleratorManager {
    public KeyStroke getKeyStroke(String actionName);
    public KeyStroke parseKeyStroke(String value);
}
