package kkckkc.jsourcepad.model;

import com.google.common.collect.Lists;
import kkckkc.syntaxpane.model.Interval;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Finder {
    private final Buffer buffer;
    private final Pattern pattern;
    private final Interval scope;
    private final Options options;
    private final String searchFor;
    private String replacement;

    public Finder(Buffer buffer, Interval scope, String searchFor, Options options) {
        this.buffer = buffer;
        this.scope = scope == null ? buffer.getCompleteDocument() : scope;
        this.options = options;
        this.searchFor = searchFor;

        this.pattern = makePattern(searchFor);
    }

    private Pattern makePattern(String searchFor) {
        if (! options.isRegexp()) searchFor = Pattern.quote(searchFor);

        return Pattern.compile(searchFor, options.isCaseSensitive() ? Pattern.CASE_INSENSITIVE : 0);
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public Interval forward(int position) {
        Interval interval = findForward(position);
        if (interval != null) {
            buffer.setSelection(interval);
        }

        return interval;
    }

    public Interval backward(int position) {
        Interval interval = findBackward(position);
        if (interval != null) {
            buffer.setSelection(interval);
        }

        return interval;
    }

    public void replace() {
        Interval selectionInterval = buffer.getSelection();
        String selection = buffer.getText(selectionInterval);

        Matcher matcher = pattern.matcher(selection);

        if (! matcher.matches()) return;

        buffer.replaceText(buffer.getSelection(), matcher.replaceAll(replacement), null);
        buffer.setSelection(selectionInterval);
    }

    public Interval getScope() {
        return this.scope;
    }

    public Options getOptions() {
        return this.options;
    }

    public String getSearchFor() {
        return this.searchFor;
    }

    private Interval findForward(int position) {
        String text = buffer.getText(new Interval(position, scope.getEnd()));
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Interval.offset(new Interval(matcher.start(), matcher.end()), position);
        }

        if (! options.isWrapAround()) return null;

        text = buffer.getText(new Interval(scope.getStart(), position));
        matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Interval.offset(new Interval(matcher.start(), matcher.end()), scope.getStart());
        }

        return null;
    }

    private Interval findBackward(int position) {
        String text = buffer.getText(new Interval(scope.getStart(), position));

        List<Interval> matches = Lists.newArrayList();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            matches.add(Interval.offset(new Interval(matcher.start(), matcher.end()), scope.getStart()));
        }

        if (! matches.isEmpty()) return matches.get(matches.size() - 1);

        if (! options.isWrapAround()) return null;

        text = buffer.getText(new Interval(position, scope.getEnd()));
        matcher = pattern.matcher(text);
        matches.clear();
        while (matcher.find()) {
            matches.add(Interval.offset(new Interval(matcher.start(), matcher.end()), position));
        }

        if (! matches.isEmpty()) return matches.get(matches.size() - 1);

        return null;
    }

    public void replaceAll(Interval scope) {
        int position = scope == null ? 0 : scope.getStart();
        int end = scope == null ? 0 : Integer.MAX_VALUE;

        Interval interval;
        while ((interval = forward(position)) != null) {
            if (interval.getStart() >= end) return;
            
            position = interval.getEnd();
            replace();
        }
    }

    public static class Options {
        private boolean wrapAround;
        private boolean regexp;
        private boolean caseSensitive;

        public boolean isCaseSensitive() {
            return caseSensitive;
        }

        public void setCaseSensitive(boolean caseSensitive) {
            this.caseSensitive = caseSensitive;
        }

        public boolean isRegexp() {
            return regexp;
        }

        public void setRegexp(boolean regexp) {
            this.regexp = regexp;
        }

        public boolean isWrapAround() {
            return wrapAround;
        }

        public void setWrapAround(boolean wrapAround) {
            this.wrapAround = wrapAround;
        }
    }

}
