package kkckkc.jsourcepad.util.ui;

import kkckkc.jsourcepad.util.io.ScriptExecutor;

import java.awt.*;

public interface ProgressDialog {
    void show(String title, ScriptExecutor.Execution execution, Container parent);

    void close();
}
