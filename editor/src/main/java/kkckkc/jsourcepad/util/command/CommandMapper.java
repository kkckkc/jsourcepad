package kkckkc.jsourcepad.util.command;

public interface CommandMapper {
    public Command read(Object externalRepresentation);
    public Object write(Command command);
}
