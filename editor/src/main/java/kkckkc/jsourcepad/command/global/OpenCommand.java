package kkckkc.jsourcepad.command.global;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.Cygwin;
import kkckkc.jsourcepad.util.command.Command;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

public class OpenCommand implements Command {
    private boolean openInSeparateWindow = false;
    private Object file;
    private Window window;
    private String content;

    public OpenCommand() {
    }

    public OpenCommand(Object file, boolean openInSeparateWindow) {
        this.file = file;
        this.openInSeparateWindow = openInSeparateWindow;
    }

    public void setOpenInSeparateWindow(boolean openInSeparateWindow) {
        this.openInSeparateWindow = openInSeparateWindow;
    }

    public void setFile(Object file) {
        this.file = file;
    }

    public Window getWindow() {
        return window;
    }

    public void setContents(String content) {
        this.content = content;
    }

    @Override
    public void execute() {
        if (file != null && file instanceof String) {
            file = new File(Cygwin.toFile((String) file));
        }

        if (openInSeparateWindow) {
            window = Application.get().getWindowManager().newWindow(null);
            if (file == null) {
                Doc doc = window.getDocList().create();
                try {
                    int idx = content.indexOf("\n");
                    String firstLine = idx == -1 ? content : content.substring(0, idx);

                    doc.getActiveBuffer().setText(
                            Application.get().getLanguageManager().getLanguage(firstLine, new File("")),
                            new BufferedReader(new StringReader(content)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                window.getDocList().open((File) file);
            }
        } else {
            try {
                window = Application.get().open((File) file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
