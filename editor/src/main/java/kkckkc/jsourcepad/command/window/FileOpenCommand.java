package kkckkc.jsourcepad.command.window;

import kkckkc.jsourcepad.util.Cygwin;
import kkckkc.jsourcepad.util.command.AbstractWindowCommand;

import java.io.File;

public class FileOpenCommand extends AbstractWindowCommand {
    private Object file;

    public FileOpenCommand() {
    }

    public FileOpenCommand(Object file) {
        this.file = file;
    }

    public void setFile(Object file) {
        this.file = file;
    }

    @Override
    public void execute() {
        if (file instanceof String) {
            file = new File(Cygwin.toFile((String) file));
        }
        window.getDocList().open((File) file);
    }
}
