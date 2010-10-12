package kkckkc.jsourcepad.command;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.command.Command;

public class CompletionCommand implements Command {
    public static enum Direction { NEXT, PREVIOUS }

    private Direction direction;

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void execute(Window window) {
        Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();
        if (direction == Direction.NEXT) {
            buffer.getCompletionManager().completeNext();
        } else {
            buffer.getCompletionManager().completePrevious();
        }
    }
}
