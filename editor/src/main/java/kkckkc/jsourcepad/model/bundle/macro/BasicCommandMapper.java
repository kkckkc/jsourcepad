package kkckkc.jsourcepad.model.bundle.macro;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import kkckkc.jsourcepad.command.window.FindCommand;
import kkckkc.jsourcepad.util.command.Command;
import kkckkc.utils.Pair;

import java.util.List;
import java.util.Map;

public class BasicCommandMapper extends AbstractCommandMapper {
    protected List<Mapping> mappings = Lists.newArrayList();
    private Class<? extends Command> commandClass;
    private String textmateAction;

    public BasicCommandMapper() {
    }

    public BasicCommandMapper(String textmateAction, Class<? extends Command> commandClass) {
        this.textmateAction = textmateAction;
        this.commandClass = commandClass;
    }

    protected void setJsourcepadClass(Class<? extends Command> commandClass) {
        this.commandClass = commandClass;
    }

    protected void setTextmateAction(String textmateAction) {
        this.textmateAction = textmateAction;
    }

    @Override
    public Pair<Class, Map<String, ?>> decode(String action, Map<String, ?> arguments) {
        if (! textmateAction.equals(action)) return null;

        Map<String, ?> args = Maps.newHashMap();
        doDecode(mappings, arguments, args);
        return new Pair<Class, Map<String, ?>>(commandClass, args);
    }

    @Override
    public Pair<String, Map<String, ?>> encode(Class type, Map<String, ?> arguments) {
        if (! type.equals(commandClass)) return null;

        Map<String, ?> args = Maps.newHashMap();
        doEncode(mappings, arguments, args);
        return new Pair<String, Map<String, ?>>(textmateAction, args);
    }

}
