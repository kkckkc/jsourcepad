package kkckkc.jsourcepad.command.window;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.util.command.AbstractWindowCommand;
import kkckkc.jsourcepad.util.command.CommandProperty;

public class CompletionCommand extends AbstractWindowCommand {
    public static enum Direction { NEXT, PREVIOUS }

    @CommandProperty private Direction direction;

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void execute() {
        Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();
        if (direction == Direction.NEXT) {
            buffer.getCompletionManager().completeNext();
        } else {
            buffer.getCompletionManager().completePrevious();
        }
    }
}
