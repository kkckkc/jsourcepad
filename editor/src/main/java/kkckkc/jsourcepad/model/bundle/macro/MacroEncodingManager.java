package kkckkc.jsourcepad.model.bundle.macro;

import com.google.common.collect.Lists;
import kkckkc.jsourcepad.command.window.TextIndentLineCommand;
import kkckkc.jsourcepad.command.window.TextAlignLeftCommand;
import kkckkc.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class MacroEncodingManager {
    private static Logger logger = LoggerFactory.getLogger(MacroEncodingManager.class);

    private static List<Mapper> mappers = Lists.newArrayList();
    static {
        mappers.add(new FindCommandMapper());
        mappers.add(new BasicCommandMapper("indent:", TextIndentLineCommand.class));
        mappers.add(new BasicCommandMapper("alignLeft:", TextAlignLeftCommand.class));
    }

    public static List<Pair<Class, Map<String, ?>>> decodeTextMateFormat(List<Map<String, ?>> commands) {
        List<Pair<Class, Map<String, ?>>> result = Lists.newArrayList();
        for (Map<String, ?> command : commands) {
            boolean found = false;
            for (Mapper mapper : mappers) {
                Pair<Class, Map<String, ?>> c = mapper.decode((String) command.get("command"), (Map<String, ?>) command.get("argument"));
                if (c == null) continue;

                result.add(c);
                found = true;
                break;
            }

            if (! found) {
                logger.warn("Cannot find mapper for " + command);
            }
        }
        return result;
    }

    public static List<Pair<String, Map<String, ?>>> encodeTextMateFormat(List<Pair<Class, Map<String, ?>>> commands) {
        List<Pair<String, Map<String, ?>>> results = Lists.newArrayList();
        for (Pair<Class, Map<String, ?>> command : commands) {
            boolean found = false;
            for (Mapper mapper : mappers) {
                Pair<String, Map<String, ?>> m = mapper.encode(command.getFirst(), command.getSecond());
                if (m == null) continue;

                results.add(m);
                found = true;
                break;
            }

            if (! found) {
                logger.warn("Cannot find mapper for " + command);
            }
        }
        return results;
    }

    public interface Mapper {
        public Pair<Class, Map<String, ?>> decode(String action, Map<String, ?> arguments);
        public Pair<String, Map<String, ?>> encode(Class type, Map<String, ?> arguments);
    }
}
