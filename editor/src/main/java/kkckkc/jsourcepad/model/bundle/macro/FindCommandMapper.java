package kkckkc.jsourcepad.model.bundle.macro;

import kkckkc.jsourcepad.command.window.FindCommand;

public class FindCommandMapper extends BasicCommandMapper {

    public FindCommandMapper() {
        setTextmateAction("findWithOptions:");
        setJsourcepadClass(FindCommand.class);

        mappings.add(new Mapping("action", "action", new Mapper<String, FindCommand.Action>() {
            @Override
            public FindCommand.Action fromTextMate(String s) {
                if ("replaceAll".equals(s)) return FindCommand.Action.REPLACE_ALL;
                return FindCommand.Action.valueOf(s);
            }

            @Override
            public String toTextMate(FindCommand.Action action) {
                if (action == FindCommand.Action.REPLACE_ALL) return "replaceAll";
                return action.name();
            }
        }));
        mappings.add(new Mapping("findInProjectIgnoreCase", null));
        mappings.add(new Mapping("findInProjectRegularExpression", null));
        mappings.add(new Mapping("findString", "findString"));
        mappings.add(new Mapping("ignoreCase", "caseSensitive", INVERT));
        mappings.add(new Mapping("regularExpression", "regularExpression"));
        mappings.add(new Mapping("replaceAllScope", null));
        mappings.add(new Mapping("replaceString", "replaceString"));
        mappings.add(new Mapping("wrapAround", "wrapAround"));
    }

}