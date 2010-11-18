package kkckkc.jsourcepad.model.bundle;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class TextmateKeystrokeEncoding {
	public static KeyStroke parse(String s) {
        // TODO: Formalize this hack
        //if ("@&".equals(s)) {
        //    s = "$@6";
        //}

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

    public static String toString(KeyStroke ks) {
        StringBuilder b = new StringBuilder();

        if ((ks.getModifiers() & KeyEvent.CTRL_MASK) != 0) b.append("^");
        if ((ks.getModifiers() & KeyEvent.ALT_MASK) != 0) b.append("~");
        if ((ks.getModifiers() & KeyEvent.META_MASK) != 0) b.append("@");

        // Handle SHIFT
        boolean uppercase = false;
        if ((ks.getModifiers() & KeyEvent.SHIFT_MASK) != 0) {
            if (Character.isLetter(ks.getKeyChar())) {
                uppercase = true;
            } else {
                b.append("$");
            }
        }

        switch (ks.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                b.append((char) 13);
                break;
            case KeyEvent.VK_F1:
                b.append((char) 63236);
                break;
            case KeyEvent.VK_F2:
                b.append((char) 63237);
                break;
            case KeyEvent.VK_F3:
                b.append((char) 63238);
                break;
            case KeyEvent.VK_F4:
                b.append((char) 63239);
                break;
            case KeyEvent.VK_F5:
                b.append((char) 63240);
                break;
            case KeyEvent.VK_F6:
                b.append((char) 63241);
                break;
            case KeyEvent.VK_F7:
                b.append((char) 63242);
                break;
            case KeyEvent.VK_F8:
                b.append((char) 63243);
                break;
            case KeyEvent.VK_F9:
                b.append((char) 63244);
                break;
            case KeyEvent.VK_F10:
                b.append((char) 63245);
                break;
            case KeyEvent.VK_F11:
                b.append((char) 63246);
                break;
            case KeyEvent.VK_F12:
                b.append((char) 63247);
                break;
            case KeyEvent.VK_F13:
                b.append((char) 63248);
                break;
            case KeyEvent.VK_F14:
                b.append((char) 63249);
                break;
            case KeyEvent.VK_F15:
                b.append((char) 63250);
                break;
            case KeyEvent.VK_CONTEXT_MENU:
                b.append((char) 3);
                break;
            case KeyEvent.VK_DELETE:
                b.append((char) 63272);
                break;
            case KeyEvent.VK_BACK_SPACE:
                b.append((char) 127);
                break;
            case KeyEvent.VK_LEFT:
                b.append((char) 63234);
                break;
            case KeyEvent.VK_RIGHT:
                b.append((char) 63235);
                break;
            case KeyEvent.VK_DOWN:
                b.append((char) 63233);
                break;
            case KeyEvent.VK_UP:
                b.append((char) 63232);
                break;
            case KeyEvent.VK_ESCAPE:
                b.append((char) 27);
                break;

            default:
                char c = ks.getKeyChar();
                if (! Character.isDefined(c)) {
                    c = (char) ks.getKeyCode();
                }

                if (uppercase) {
                    b.append(Character.toUpperCase(c));
                } else {
                    b.append(Character.toLowerCase(c));
                }
                break;
        }

        return b.toString();
    }
}
