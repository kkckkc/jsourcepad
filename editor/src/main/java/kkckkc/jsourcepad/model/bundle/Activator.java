package kkckkc.jsourcepad.model.bundle;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import kkckkc.syntaxpane.model.Scope;
import kkckkc.syntaxpane.style.ScopeSelector;


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
//	
//	public KeyStroke getKeyStroke() {
//	    return keyStroke;
//    }
	
	public String toString() {
        String acceleratorText = "";
        if (keyStroke != null) {
        	acceleratorText += "(";
        	
//        	acceleratorText += keyStroke;
        	
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
		if (keyStroke == null) return false;
		
		char keyChar = ks.getKeyChar();
		int keyCode = ks.getKeyCode();

		if (keyChar < 0x20) {
			if (keyChar != keyCode) {
				keyChar += 0x40;

				if ((keyChar >= 'A') && (keyChar <= 'Z')) {
					keyChar += 0x20;
				}
			}
		}
		
		return 
			(keyStroke.getModifiers() & 0xF) == (ks.getModifiers() & 0xF) && 
			keyCode != 0 && 
			(keyStroke.getKeyCode() == keyCode || 
					(keyChar != KeyEvent.CHAR_UNDEFINED && Character.toLowerCase(keyStroke.getKeyChar()) == keyChar));
    }

	public ScopeSelector getScopeSelector() {
	    return scopeSelector;
    }

	public boolean matches(String trigger) {
	    return this.tabTrigger != null && this.tabTrigger.equals(trigger);
    }
}
