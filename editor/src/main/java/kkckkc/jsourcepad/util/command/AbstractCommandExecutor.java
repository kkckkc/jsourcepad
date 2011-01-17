package kkckkc.jsourcepad.util.command;

import kkckkc.jsourcepad.model.Application;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public abstract class AbstractCommandExecutor implements CommandExecutor {
    protected abstract void prepareCommand(Command command);

    @Override
    public void execute(final Command command) {
        if (! EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(new EventQueueContinuation(command));
        } else {
            prepareCommand(command);
            Application.get().topic(CommandExecutor.Listener.class).post().commandExecuted(command);
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
            prepareCommand(command);
            Application.get().topic(CommandExecutor.Listener.class).post().commandExecuted(command);
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
            prepareCommand(command);
            Application.get().topic(CommandExecutor.Listener.class).post().commandExecuted(command);
            doExecute(command);
        }
    }

    protected abstract void doExecute(Command command);
}
