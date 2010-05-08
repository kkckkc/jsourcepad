package kkckkc.jsourcepad.model.bundle;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

public class KeystrokeParser {
	public static KeyStroke parse(String s) {
		int modifiers = 0;
		char[] c = s.toCharArray();
		if (c.length > 1) {
			for (int i = 0; i < (c.length - 1); i++) {
				if (c[i] == '^') {
					modifiers |= KeyEvent.CTRL_MASK;
				} else if (c[i] == '@') {
					modifiers |= KeyEvent.META_MASK;
				} else if (c[i] == '~') {
					modifiers |= KeyEvent.ALT_MASK;
				} else if (c[i] == '$') {
					modifiers |= KeyEvent.SHIFT_MASK;
				}
			}
		}
		
		char key = c[c.length - 1];
		if (Character.isUpperCase(key)) {
			modifiers |= KeyEvent.SHIFT_MASK;
		}
		
		key = Character.toUpperCase(key);
		int keycode = (int) key;
		
		KeyStroke ks;
		if (keycode == 13) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, modifiers);
		} else if (keycode == 63236) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_F1, modifiers);
		} else if (keycode == 63237) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_F2, modifiers);
		} else if (keycode == 63238) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_F3, modifiers);
		} else if (keycode == 63239) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_F4, modifiers);
		} else if (keycode == 63240) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_F5, modifiers);
		} else if (keycode == 63241) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_F6, modifiers);
		} else if (keycode == 63242) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_F7, modifiers);
		} else if (keycode == 63243) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_F8, modifiers);
		} else if (keycode == 63244) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_F9, modifiers);
		} else if (keycode == 63245) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_F10, modifiers);
		} else if (keycode == 63246) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_F11, modifiers);
		} else if (keycode == 63247) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_F12, modifiers);
		} else if (keycode == 63248) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_F13, modifiers);
		} else if (keycode == 63249) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_F14, modifiers);
		} else if (keycode == 63250) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_F15, modifiers);
		} else if (keycode == 3) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_CONTEXT_MENU, modifiers);
		} else if (keycode == 63272) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, modifiers);
		} else if (keycode == 127) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, modifiers);
		} else if (keycode == 63234) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, modifiers);
		} else if (keycode == 63235) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, modifiers);
		} else if (keycode == 63233) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, modifiers);
		} else if (keycode == 63232) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_UP, modifiers);
		
			
		} else if (keycode == 27) {
			ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, modifiers);
			
		} else {
			ks = KeyStroke.getKeyStroke(new Character(key), modifiers);
		}
		
		return ks;
	}
}
