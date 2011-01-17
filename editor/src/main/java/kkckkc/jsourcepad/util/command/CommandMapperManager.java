package kkckkc.jsourcepad.util.command;

import kkckkc.utils.Pair;

import java.util.Map;

public interface CommandMapperManager {
    public Command fromExternalRepresentation(Pair<Class, Map<String, ?>> externalRepresentation);
    public Pair<Class, Map<String, ?>> toExternalRepresentation(Command command);
}
