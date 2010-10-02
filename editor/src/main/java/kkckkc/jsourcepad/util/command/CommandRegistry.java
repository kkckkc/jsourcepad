package kkckkc.jsourcepad.util.command;

public interface CommandRegistry {
    public Class<? extends Command> getCommandClass(String id);
    public String getCommandId(Class<? extends Command> commandClass);
}
