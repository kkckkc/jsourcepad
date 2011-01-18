package kkckkc.syntaxpane.regex;

public class LiteralPatternFactory implements PatternFactory {
    @Override
    public Pattern create(String s) {
        return create(s, 0);
    }

    @Override
    public Pattern create(String s, int options) {
        return new LiteralPattern(s, options);
    }

    public static class LiteralPattern implements Pattern {
        private String pattern;
        private int options;

        public LiteralPattern(String pattern, int options) {
            this.pattern = pattern;
            this.options = options;
        }

        @Override
        public Matcher matcher(CharSequence cs) {
            return new LiteralMatcher(pattern, options, cs);
        }

        @Override
        public String pattern() {
            return pattern;
        }
    }

    private static class LiteralMatcher implements Matcher {

        private String pattern;
        private int options;
        private String text;

        private int start = -1;
        private int end = -1;

        public LiteralMatcher(String pattern, int options, CharSequence text) {
            this.pattern = pattern;
            this.options = options;
            if ((options & PatternFactory.CASE_INSENSITIVE) != 0) {
                this.text = text.toString().toLowerCase();
                this.pattern = this.pattern.toLowerCase();
            } else {
                this.text = text.toString();
            }
        }

        @Override
        public boolean matches() {
            return find(0);
        }

        @Override
        public boolean matchesAll() {
            find(0);
            return start == 0 && end == text.length();
        }

        @Override
        public int start() {
            return start;
        }

        @Override
        public boolean find(int position) {
            start = text.indexOf(pattern, position);
            end = start + pattern.length();
            return start >= 0;
        }

        @Override
        public boolean find() {
            return find(0);
        }

        @Override
        public int end() {
            return end;
        }

        @Override
        public int groupCount() {
            return 0;
        }

        @Override
        public String group(int i) {
            return null;
        }

        @Override
        public int start(int subPatternIdx) {
            return 0;
        }

        @Override
        public int end(int subPatternIdx) {
            return 0;
        }

        @Override
        public int start(String subPattern) {
            return 0;
        }

        @Override
        public int end(String subPattern) {
            return 0;
        }

        @Override
        public String replaceAll(String replacement) {
            return text.replaceAll(java.util.regex.Pattern.quote(pattern),
                    java.util.regex.Matcher.quoteReplacement(replacement));
        }

        @Override
        public String replacementString(String replacement) {
            return replacement;
        }
    }
}
