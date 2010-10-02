package kkckkc.jsourcepad.util.command;

public interface CommandMapper {
    public Command read(Object externalRepresentation, CommandRegistry commandRegistry);
    public Object write(Command command, CommandRegistry commandRegistry);
}
