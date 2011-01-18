package kkckkc.jsourcepad.model;

import com.google.common.collect.Lists;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.syntaxpane.regex.*;

import java.util.List;

public class Finder {
    private final Buffer buffer;
    private final Pattern pattern;
    private Interval scope;
    private final Options options;
    private final String searchFor;
    private String replacement;

    private Matcher matcher;
    private Interval foundInterval;

    public Finder(Buffer buffer, Interval scope, String searchFor, Options options) {
        this.buffer = buffer;
        this.scope = scope == null ? buffer.getCompleteDocument() : scope;
        this.options = options;
        this.searchFor = searchFor;

        this.pattern = makePattern(searchFor);
    }

    private Pattern makePattern(String searchFor) {
        PatternFactory patternFactory = new JoniPatternFactory();
        if (! options.isRegexp())
            patternFactory = new LiteralPatternFactory();

        return patternFactory.create(searchFor, options.isCaseSensitive() ? 0 : PatternFactory.CASE_INSENSITIVE);
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
        String actualReplacement = doReplace();

        buffer.setSelection(Interval.createWithLength(foundInterval.getStart(), actualReplacement.length()));
        foundInterval = null;
    }

    private String doReplace() {
        String actualReplacement = matcher.replacementString(replacement);
        buffer.replaceText(foundInterval, actualReplacement, null);

        if (scope.contains(foundInterval.getStart())) {
            scope = Interval.createWithLength(scope.getStart(), scope.getLength() - (foundInterval.getLength() - actualReplacement.length()));
        } else if (foundInterval.getStart() <= scope.getStart()) {
            scope = Interval.createWithLength(scope.getStart() - (foundInterval.getLength() - actualReplacement.length()), scope.getLength());
        }
        return actualReplacement;
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
        matcher = pattern.matcher(text);
        if (matcher.find()) {
            return (foundInterval = Interval.offset(new Interval(matcher.start(), matcher.end()), position));
        }

        if (! options.isWrapAround()) return null;

        text = buffer.getText(new Interval(scope.getStart(), position));
        matcher = pattern.matcher(text);
        if (matcher.find()) {
            return (foundInterval = Interval.offset(new Interval(matcher.start(), matcher.end()), scope.getStart()));
        }

        return (foundInterval = null);
    }

    private Interval findBackward(int position) {
        String text = buffer.getText(new Interval(scope.getStart(), position));

        List<Interval> matches = Lists.newArrayList();
        matcher = pattern.matcher(text);
        int p = 0;
        while (matcher.find(p)) {
            matches.add(Interval.offset(new Interval(matcher.start(), matcher.end()), scope.getStart()));
            p = matcher.end();
        }

        if (! matches.isEmpty()) return (foundInterval = matches.get(matches.size() - 1));

        if (! options.isWrapAround()) return (foundInterval = null);

        text = buffer.getText(new Interval(position, scope.getEnd()));
        matcher = pattern.matcher(text);
        matches.clear();
        while (matcher.find()) {
            matches.add(Interval.offset(new Interval(matcher.start(), matcher.end()), position));
        }

        if (! matches.isEmpty()) return (foundInterval = matches.get(matches.size() - 1));

        return (foundInterval = null);
    }

    public void replaceAll(Interval scope) {
        if (scope == null) scope = buffer.getCompleteDocument();
        int position = scope.getStart();
        int end = scope.getEnd();

        Interval interval;
        while ((interval = findForward(position)) != null) {
            if (interval.getStart() >= end) return;

            doReplace();

            if (scope.contains(position)) {
                scope = Interval.createWithLength(scope.getStart(), scope.getLength() - (interval.getLength() - replacement.length()));
            } else if (position <= scope.getStart()) {
                scope = Interval.createWithLength(scope.getStart() - (interval.getLength() - replacement.length()), scope.getLength());
            }

            position = interval.getStart() + replacement.length();
            if (position >= buffer.getLength()) return;
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
