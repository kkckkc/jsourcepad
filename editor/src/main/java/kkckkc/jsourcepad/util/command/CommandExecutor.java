package kkckkc.jsourcepad.util.command;

public interface CommandExecutor {
    public void execute(Command command);
    public void executeSync(Command command);

    public interface Listener {
        public void commandExecuted(Command command);
    }
}
