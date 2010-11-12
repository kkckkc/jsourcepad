package kkckkc.jsourcepad.commitdialog;

import javax.swing.*;
import java.io.IOException;
import java.io.Writer;

public interface Dialog {
    public int execute(JFrame parent, Writer out, String... args) throws IOException;

}
