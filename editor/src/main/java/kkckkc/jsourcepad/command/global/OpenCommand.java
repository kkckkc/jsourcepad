package kkckkc.jsourcepad.command.global;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.Cygwin;
import kkckkc.jsourcepad.util.command.Command;

import java.io.File;
import java.io.IOException;

public class OpenCommand implements Command {
    private boolean openInSeparateWindow = false;
    private Object file;
    private Window window;

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

    @Override
    public void execute() {
        if (file instanceof String) {
            file = new File(Cygwin.toFile((String) file));
        }

        if (openInSeparateWindow) {
            window = Application.get().getWindowManager().newWindow(null);
            window.getDocList().open((File) file);
        } else {
            try {
                window = Application.get().open((File) file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
