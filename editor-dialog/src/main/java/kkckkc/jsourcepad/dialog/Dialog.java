package kkckkc.jsourcepad.dialog;

import kkckkc.jsourcepad.model.Window;

import java.io.IOException;
import java.io.Writer;

public interface Dialog {
    public int execute(Window window, Writer out, String pwd, String stdin, String... args) throws IOException;

}
