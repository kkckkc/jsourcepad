package kkckkc.jsourcepad.model.bundle;

import junit.framework.TestCase;
import kkckkc.utils.plist.GeneralPListReader;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class TextmateKeystrokeEncodingTest extends TestCase {

    public void testForAllBundles() throws IOException {
        File root = new File("/Users/magnus/.jsourcepad/Shared/Bundles");
        recurse(root);
    }

    private void recurse(File root) throws IOException {
        for (File f : root.listFiles()) {
            if (f.isDirectory()) recurse(f);
            else if (BundleStructure.isOfAnyType(f)) {
                Map m = (Map) new GeneralPListReader().read(f);
                String keyEquivalent = (String) m.get("keyEquivalent");
                if (keyEquivalent == null || "".equals(keyEquivalent)) continue;

                KeyStroke ks = TextmateKeystrokeEncoding.parse(keyEquivalent);

                check(keyEquivalent, TextmateKeystrokeEncoding.toString(ks));
            }
        }
    }

    private void check(String s1, String s2) {
        int modifiers1 = getModifiers(s1);
        int modifiers2 = getModifiers(s2);

        assertEquals("Modifiers are different", modifiers1, modifiers2);
        assertEquals("Key is different", s1.charAt(s1.length() - 1), s2.charAt(s2.length() - 1));
    }

    private int getModifiers(String s1) {
        int modifiers = 0;
        char[] c = s1.toCharArray();
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
        return modifiers;
    }

}
