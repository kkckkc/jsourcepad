package kkckkc.jsourcepad.model.bundle;

import kkckkc.jsourcepad.util.ui.KeyStrokeUtils;
import kkckkc.syntaxpane.style.ScopeSelector;

import javax.swing.*;
import java.awt.event.KeyEvent;


public class Activator {

	private KeyStroke keyStroke;
	private String tabTrigger;
	private ScopeSelector scopeSelector;

	public Activator(KeyStroke ks, String tabTrigger, ScopeSelector scopeSelector) {
	    this.keyStroke = ks;
	    this.tabTrigger = tabTrigger;
	    this.scopeSelector = scopeSelector;
    }

	public String getTabTrigger() {
	    return tabTrigger;
    }
	
	public String toString() {
        String acceleratorText = "";
        if (keyStroke != null) {
        	acceleratorText += "(";
        	
            int modifiers = keyStroke.getModifiers();
            if (modifiers > 0) {
                acceleratorText += KeyEvent.getKeyModifiersText(modifiers);
                acceleratorText += "-";
            }

	        int keyCode = keyStroke.getKeyCode();
	        if (keyCode != 0) {
	            acceleratorText += KeyEvent.getKeyText(keyCode);
	        } else {
	            acceleratorText += keyStroke.getKeyChar();
	        }

	        acceleratorText += ")";
        } else if (tabTrigger != null) {
        	acceleratorText = tabTrigger + " \u21e5";
        }
		
		return acceleratorText;
	}

	public KeyStroke getKeyStroke() {
	    return keyStroke;
    }

	public boolean matches(KeyEvent ks) {
        return KeyStrokeUtils.matches(keyStroke, ks);
    }

	public ScopeSelector getScopeSelector() {
	    return scopeSelector;
    }

	public boolean matches(String trigger) {
	    return this.tabTrigger != null && this.tabTrigger.equals(trigger);
    }
}
