package kkckkc.jsourcepad.util.command;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public abstract class AbstractCommandExecutor implements CommandExecutor {
    @Override
    public void execute(final Command command) {
        if (! EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(new EventQueueContinuation(command));
        } else {
            doExecute(command);
        }
    }

    @Override
    public void executeSync(final Command command) {
        if (! EventQueue.isDispatchThread()) {
            try {
                EventQueue.invokeAndWait(new EventQueueContinuation(command));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            doExecute(command);
        }
    }

    class EventQueueContinuation implements Runnable {
        private Command command;

        EventQueueContinuation(Command command) {
            this.command = command;
        }

        @Override
        public void run() {
            doExecute(command);
        }
    }

    protected abstract void doExecute(Command command);
}
