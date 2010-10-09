package kkckkc.syntaxpane.regex;

import org.joni.Option;
import org.joni.Regex;
import org.joni.Region;
import org.joni.Syntax;
import org.joni.Syntax.MetaCharTable;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.joni.constants.MetaChar.INEFFECTIVE_META_CHAR;

public class JoniPatternFactory implements PatternFactory {

	private static final int MAX_ENTRIES = 100;
	private static Map<String, JoniPattern> CACHE = new LinkedHashMap<String, JoniPattern>(MAX_ENTRIES, .75F, true) {  
	    protected boolean removeEldestEntry(Map.Entry<String, JoniPattern> eldest) {  
	      return size() > MAX_ENTRIES;  
	    }  
	  };  
	
	private static final Syntax SYNTAX = new Syntax(
	        ((  Syntax.GNU_REGEX_OP | Syntax.OP_QMARK_NON_GREEDY |
        		Syntax.OP_ESC_OCTAL3 | Syntax.OP_ESC_X_HEX2 |
        		Syntax.OP_ESC_C_CONTROL )
                & ~Syntax.OP_ESC_LTGT_WORD_BEGIN_END ),
                
                ( Syntax.OP2_QMARK_GROUP_EFFECT |
                		Syntax.OP2_OPTION_RUBY |
                		Syntax.OP2_QMARK_LT_NAMED_GROUP | Syntax.OP2_ESC_K_NAMED_BACKREF |
                		Syntax.OP2_ESC_G_SUBEXP_CALL |
                		Syntax.OP2_ESC_P_BRACE_CHAR_PROPERTY  |
                		Syntax.OP2_ESC_P_BRACE_CIRCUMFLEX_NOT |
                		Syntax.OP2_PLUS_POSSESSIVE_REPEAT |
                		Syntax.OP2_CCLASS_SET_OP | Syntax.OP2_ESC_CAPITAL_C_BAR_CONTROL |
                		Syntax.OP2_ESC_CAPITAL_M_BAR_META | Syntax.OP2_ESC_V_VTAB |
                		Syntax.OP2_ESC_H_XDIGIT ),
                
                ( Syntax.GNU_REGEX_BV | 
                		Syntax.ALLOW_INTERVAL_LOW_ABBREV |
                		Syntax.DIFFERENT_LEN_ALT_LOOK_BEHIND |
//	                		Syntax.CAPTURE_ONLY_NAMED_GROUP |
                		Syntax.ALLOW_EMPTY_RANGE_IN_CC |
                		Syntax.ALLOW_MULTIPLEX_DEFINITION_NAME |
                		Syntax.FIXED_INTERVAL_IS_GREEDY_ONLY |
                		Syntax.WARN_CC_OP_NOT_ESCAPED |
                		Syntax.WARN_REDUNDANT_NESTED_REPEAT ),
                
                Option.NONE,
                
                new MetaCharTable(
                    '\\',                           /* esc */
                    INEFFECTIVE_META_CHAR,          /* anychar '.' */
                    INEFFECTIVE_META_CHAR,          /* anytime '*' */
                    INEFFECTIVE_META_CHAR,          /* zero or one time '?' */
                    INEFFECTIVE_META_CHAR,          /* one or more time '+' */
                    INEFFECTIVE_META_CHAR           /* anychar anytime */
                )
            );

	
	@Override
    public Pattern create(String s) {
		synchronized (CACHE) {
			if (CACHE.containsKey(s)) {
				return CACHE.get(s);
			}

			JoniPattern p = new JoniPattern(s.toCharArray());
			CACHE.put(s, p);
			return p;
		}
	}
	
	private static char[] getCharArray(CharSequence cs) {
		if (cs instanceof String) {
			return ((String) cs).toCharArray();
		} else {
			System.out.println("Warning: CharSequence is " + cs.getClass());
						
			int len = cs.length();
			char[] dest = new char[len];
			for (int i = 0; i < len; i++) {
				dest[i] = cs.charAt(i);
			}
			
			return dest;
		}
	}
	
	public static class JoniPattern implements Pattern {
		private Regex re;
		private char[] sc;

		public JoniPattern(char[] ch) {
	        this.sc = ch;
        }

		@Override
        public Matcher matcher(CharSequence cs) {
			char[] chars = getCharArray(cs);
			makeRe();
	        return new JoniMatcher(re.matcher(chars), chars);
        }

		private void makeRe() {
	        if (re == null) {
	    		try {
	    			this.re = new Regex(sc, 0, sc.length, Option.DEFAULT, null, SYNTAX);
	    		} catch (Exception e) {
	    			throw new RuntimeException("Invalid regexp: " + new String(sc), e);
	    		}
	        }
        }

		@Override
        public String pattern() {
	        return new String(sc);
        }
		
	}
	
	public static class JoniMatcher implements Matcher {
		private org.joni.Matcher matcher;
		private Region region;
		private char[] chars;

		public JoniMatcher(org.joni.Matcher matcher, char[] chars) {
	        this.matcher = matcher;
			this.chars = chars;
	        this.region = null;
        }

		@Override
        public int end() {
	        return region == null ? matcher.getEnd() : region.end[0];
        }

		@Override
        public int end(int subPatternIdx) {
			if (subPatternIdx == 0) return end();
			if (region == null) return -1;
			if (subPatternIdx >= region.end.length) return -1;
	        return region.end[subPatternIdx];
        }

		@Override
        public int end(String subPattern) {
	        throw new UnsupportedOperationException();
        }

		@Override
        public boolean find(int position) {
	        int i = matcher.search(position, this.chars.length, Option.NONE);
	        region = matcher.getRegion();
	        return i >= 0; 
        }

		@Override
        public boolean find() {
	        return find(0);
        }

		@Override
        public String group(int i) {
	        return new String(chars, start(i), end(i));
        }

		@Override
        public int groupCount() {
	        return region.beg.length;
        }

        @Override
        public boolean matchesAll() {
            return find() && start() == 0 && end() == chars.length;
        }

		@Override
        public boolean matches() {
	        return find();
        }

		@Override
        public int start() {
	        return region == null ? matcher.getBegin() : region.beg[0];
        }

		@Override
        public int start(int subPatternIdx) {
			if (subPatternIdx == 0) return start();
			if (region == null) return -1;
			if (subPatternIdx >= region.beg.length) return -1;
	        return region.beg[subPatternIdx];
        }

		@Override
        public int start(String subPattern) {
	        throw new UnsupportedOperationException();
        }

		@Override
        public String replaceAll(String replacement) {
            StringBuilder b = new StringBuilder();

            int pos = 0;
            while (find(pos)) {
                int start = start();
                int end = end();

                if (start == end) break;

                if (start > pos) {
                    b.append(new String(chars, pos, start - pos));
                }

                appendReplacement(replacement, b);

                pos = end;
            }

            if (pos < chars.length) {
                b.append(new String(chars, pos, chars.length - pos));
            }

            return b.toString();
        }

        private void appendReplacement(String replacement, StringBuilder b) {
            // Remove all conditionals
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\(\\?([0-9]+):([^):]+)(:([^)]+))?\\)");
            java.util.regex.Matcher m = pattern.matcher(replacement);

            StringBuffer buf = new StringBuffer();
            while (m.find()) {
                Integer groupId = Integer.parseInt(m.group(1));
                String value = m.group(2);
                String alternative = m.group(4);
                if (groupId <= groupCount()) {
                    m.appendReplacement(buf, value);
                } else {
                    if (alternative != null) {
                        m.appendReplacement(buf, alternative);
                    } else {
                        m.appendReplacement(buf, "");
                    }
                }
            }
            m.appendTail(buf);

            replacement = buf.toString();

            // Parse group references, escapes and case foldings
            boolean inEscapeSequence = false;

            char currentTransformation = 'E';
            char[] chars = replacement.toCharArray();
            for (int pos = 0; pos < chars.length; pos++) {
                char c = chars[pos];

                String fragment = null;
                if (! inEscapeSequence) {
                    if (c == '$') {
                        pos++;

                        StringBuilder num = new StringBuilder();
                        while (pos < chars.length && Character.isDigit(chars[pos])) {
                            num.append(chars[pos]);
                            pos++;
                        }

                        pos--;

                        fragment = group(Integer.parseInt(num.toString()));

                    } else if (c == '\\') {
                        inEscapeSequence = true;
                    } else {
                        fragment = Character.toString(c);
                    }
                } else {
                    if (c == 'L' || c == 'U' || c == 'l' || c == 'u' || c == 'E') {
                        currentTransformation = c;
                    } else if (c == 'n') {
                        fragment = "\n";
                    } else if (c == 't') {
                        fragment = "\t";
                    } else {
                        fragment = Character.toString(c);
                    }

                    inEscapeSequence = false;
                }

                if (fragment != null) {
                    switch (currentTransformation) {
                        case 'L':
                            b.append(fragment.toLowerCase());
                            break;
                        case 'U':
                            b.append(fragment.toUpperCase());
                            break;
                        case 'l':
                            b.append(Character.toLowerCase(fragment.charAt(0)));
                            if (fragment.length() > 1) b.append(fragment.substring(1));
                            currentTransformation = 'E';
                            break;
                        case 'u':
                            b.append(Character.toUpperCase(fragment.charAt(0)));
                            if (fragment.length() > 1) b.append(fragment.substring(1));
                            currentTransformation = 'E';
                            break;
                        default:
                            b.append(fragment);
                            currentTransformation = 'E';
                            break;
                    }
                }
            }

        }

    }
}