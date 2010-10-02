package kkckkc.syntaxpane.style;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class BasicScopeSelectorParser {
    public static ScopeSelector parse(String s) {
        ScopeSelector selector = new ScopeSelector();

        StringTokenizer tok = new StringTokenizer(s, ",");
        while (tok.hasMoreTokens()) {
            String t = tok.nextToken();
            selector.addRule(parseRule(t));
        }

        return selector;
    }

    public static ScopeSelector.Rule parseRule(String t) {
        List<String> positiveRule = new ArrayList<String>(5);

        // Parse positive rules
        String s = t;
        StringTokenizer tok = new StringTokenizer(s, " ");
        while (tok.hasMoreTokens()) {
            String i = tok.nextToken();
            positiveRule.add(i);
        }

        return new ScopeSelector.Rule(positiveRule.size() == 0 ? null : positiveRule, null);
    }

}
