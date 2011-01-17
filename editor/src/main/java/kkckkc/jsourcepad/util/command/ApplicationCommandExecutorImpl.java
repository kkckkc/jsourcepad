package kkckkc.jsourcepad.util.command;

public class ApplicationCommandExecutorImpl extends AbstractCommandExecutor{
    @Override
    protected void prepareCommand(Command command) {
    }

    @Override
    protected void doExecute(Command command) {
        if (command instanceof WindowCommand) {
            throw new IllegalArgumentException("Window-scoped commands need to be executed through Window.getCommandExecutor");
        }
        command.execute();
    }
}
