package kkckkc.syntaxpane.regex;

import com.google.code.regex.NamedMatcher;
import com.google.code.regex.NamedPattern;

public class NamedPatternFactory implements PatternFactory {
	public Pattern create(String s) {
		return new NamedPatternFacade(s);
	}

    @Override
    public Pattern create(String s, int options) {
        return new NamedPatternFacade(s, options);
    }

    public static class NamedPatternFacade implements Pattern {
		private NamedPattern namedPattern;

		public NamedPatternFacade(String pattern) {
			this.namedPattern = NamedPattern.compile(pattern, java.util.regex.Pattern.COMMENTS);
		}

        public NamedPatternFacade(String pattern, int options) {
            int opts = java.util.regex.Pattern.COMMENTS;
            if ((options & PatternFactory.CASE_INSENSITIVE) != 0) opts |= java.util.regex.Pattern.CASE_INSENSITIVE;
            this.namedPattern = NamedPattern.compile(pattern, opts);
        }

		@Override
        public Matcher matcher(CharSequence cs) {
	        return new NamedMatcherFacade(this.namedPattern.matcher(cs));
        }

		@Override
        public String pattern() {
	        return namedPattern.standardPattern();
        }
	}
	
	public static class NamedMatcherFacade implements Matcher {
		private NamedMatcher namedMatcher;

		public NamedMatcherFacade(NamedMatcher matcher) {
	        this.namedMatcher = matcher;
        }

		@Override
        public int end() {
	        return namedMatcher.end();
        }

		@Override
        public int end(int subPatternIdx) {
	        return namedMatcher.end(subPatternIdx);
        }

		@Override
        public int end(String subPattern) {
	        return namedMatcher.end(subPattern);
        }

		@Override
        public boolean find(int position) {
	        return namedMatcher.find(position);
        }

		@Override
        public boolean find() {
	        return namedMatcher.find(0);
        }

		@Override
        public String group(int i) {
	        return namedMatcher.group(i);
        }

		@Override
        public int groupCount() {
	        return namedMatcher.groupCount();
        }

		@Override
        public boolean matches() {
	        return namedMatcher.find();
        }

        @Override
        public boolean matchesAll() {
            return namedMatcher.matches();
        }

		@Override
        public int start() {
	        return namedMatcher.start();
        }

		@Override
        public int start(int subPatternIdx) {
	        return namedMatcher.start(subPatternIdx);
        }

		@Override
        public int start(String subPattern) {
	        return namedMatcher.start(subPattern);
        }

		@Override
        public String replaceAll(String replacement) {
	        return namedMatcher.replaceAll(replacement);
        }

        @Override
        public String replacementString(String replacement) {
            if (find()) {
                StringBuffer b = new StringBuffer();
                namedMatcher.appendReplacement(b, replacement);
                return b.substring(start());
            } else {
                return replacement;
            }
        }

    }
}
