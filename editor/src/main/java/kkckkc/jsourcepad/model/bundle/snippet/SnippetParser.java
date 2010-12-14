package kkckkc.jsourcepad.model.bundle.snippet;

import com.google.common.collect.Lists;
import kkckkc.syntaxpane.regex.JoniPatternFactory;
import kkckkc.syntaxpane.regex.PatternFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class SnippetParser {
	private static final char RIGHT_BRACE = '}';
	private static final char LEFT_BRACE = '{';
	private static final char DOLLAR_SIGN = '$';
	private static final char BACK_TICK = '`';

	private static Pattern DEFAULT_PATTERN = Pattern.compile("^([0-9A-Z_]+):(.*)$"); 
	private static Pattern TRANSFORMATION_PATTERN = Pattern.compile("^([0-9A-Z_]+)/(.*)/(.*)/(.*)$"); 
	
	public Collection<Node> parse(String s) {
        List<Node> dest = doParse(s);

        boolean endVarFound = false;
        for (Node node : dest) {
            if (node instanceof Variable) {
                Variable v = (Variable) node;
                if ("0".equals(v.getName())) {
                    endVarFound = true;
                }
            }
        }

        if (! endVarFound) {
            dest.add(new Variable("0"));
        }

		return dest;
	}

    private List<Node> doParse(String s) {
        List<Node> dest = Lists.newArrayList();

        int prev = 0;
        int tokenIdx = indexOf(s, 0, DOLLAR_SIGN, BACK_TICK);
        while (tokenIdx >= 0) {
            if (tokenIdx != 0) {
                dest.add(new Literal(s.substring(prev, tokenIdx)));
            }

            if (s.charAt(tokenIdx) == DOLLAR_SIGN) {
                prev = parseExpression(s, tokenIdx, dest);
            } else {
                prev = parseScript(s, tokenIdx, dest);
            }
            tokenIdx = indexOf(s, prev, DOLLAR_SIGN, BACK_TICK);
        }

        String str = s.substring(prev);
        if (! "".equals(str)) {
            dest.add(new Literal(str));
        }
        return dest;
    }


    private int parseScript(String s, int idx, List<Node> dest) {
		int end = s.indexOf(BACK_TICK, idx + 1);
		if (end == -1) {
			throw new RuntimeException("Unbalanced backticks");
		}
		
		dest.add(new Script(s.substring(idx + 1, end)));
		
	    return end + 1;
    }


	private int parseExpression(String s, int idx, List<Node> dest) {
		if (s.charAt(idx + 1) == LEFT_BRACE) {
			return parseAdvancedExpression(s, idx, dest);
		} else {
			return parseSimpleExpression(s, idx, dest);
		}
    }


	private int parseAdvancedExpression(String s, int idx, List<Node> dest) {
		int end = idx + 2;
		int depth = 0;
		
		while (end < s.length() && ! (s.charAt(end) == RIGHT_BRACE && depth == 0)) {
			if (s.charAt(end) == LEFT_BRACE) depth++;
			if (s.charAt(end) == RIGHT_BRACE) depth--;
			
			end++;
		}
		
		String def = s.substring(idx + 2, end);
		Matcher defMatcher = DEFAULT_PATTERN.matcher(def);
		Matcher transMatcher = TRANSFORMATION_PATTERN.matcher(def);
		
		if (defMatcher.matches()) {
			dest.add(new Variable(defMatcher.group(1), doParse(defMatcher.group(2))));
		} else if (transMatcher.matches()) {
			dest.add(new Variable(
					transMatcher.group(1), transMatcher.group(2), 
					transMatcher.group(3), transMatcher.group(4)));
		} else {
			dest.add(new Variable(def));
		}
		
		return end + 1;
    }

	private int parseSimpleExpression(String s, int idx, List<Node> dest) {
		int end = idx + 1;
		while (end < s.length() &&
				(Character.isLetter(s.charAt(end)) ||
				 Character.isDigit(s.charAt(end)) ||
				 s.charAt(end) == '_')) {
			end++;
		}

		dest.add(new Variable(s.substring(idx + 1, end)));

		return end;
/*		int end = idx + 1;
		while (end < s.length() && 
				! Character.isLetter(s.charAt(end)) &&
				! Character.isDigit(s.charAt(end)) &&
				s.charAt(end) != '_') {
			end++;
		}
		
		dest.add(new Variable(s.substring(idx + 1, end + 1)));
		
		return end + 1; */
    }

	
	private int indexOf(String s, int position, char... chars) {
		int retValue = Integer.MAX_VALUE;
		
		for (char c : chars) {
			int pos = s.indexOf(c, position);
			if (pos == -1) pos = Integer.MAX_VALUE;
			retValue = Math.min(retValue, pos);
		}
		
		return retValue == Integer.MAX_VALUE ? -1 : retValue;
	}
	

	interface Node {
		public Collection<Node> children();
		public void accept(NodeVisitor visitor);
	}

	interface NodeVisitor {
		void visit(Literal literal);
		void visit(Variable variable);
		void visit(Script script);
	}
	
	public static class Literal implements Node {
        private String string;

		public Literal(String s) {
	        this.string = s;
        }
		
		public String getString() {
	        return string;
        }
		
		@Override
        public Collection<Node> children() {
	        return Collections.emptyList();
        }

		@Override
        public void accept(NodeVisitor visitor) {
	        visitor.visit(this);
        }
	}
	
	public static class Script implements Node {
		private String body;
		
		public Script(String body) {
	        this.body = body;
        }

		@Override
        public Collection<Node> children() {
	        return Collections.emptyList();
        }

		@Override
        public void accept(NodeVisitor visitor) {
	        visitor.visit(this);
        }

		public String getBody() {
	        return body;
        }
	}
	
	public static class Variable implements Node {
		private static Pattern NUMERIC = Pattern.compile("^[0-9]+$"); 

		private String name;
		private String regexp;
		private String format;
		private String options;

		private Collection<Node> children = Collections.emptyList();

		public Variable(String name) {
	        this.name = name;
        }

		public Variable(String name, String regexp, String format, String options) {
	        this.name = name;
	        this.regexp = regexp;
	        this.format = format;
	        this.options = options;
        }

		public Variable(String name, Collection<Node> nodes) {
	        this.name = name;
	        this.children = nodes;
        }
		
		public String getName() {
	        return name;
        }
		
		public boolean isTabStop() {
	        return NUMERIC.matcher(name).matches();
        }
		
		@Override
        public Collection<Node> children() {
	        return children;
        }

		@Override
        public void accept(NodeVisitor visitor) {
	        visitor.visit(this);
        }

		public String evaluate(Map<String, String> environment) {
	        String s = environment.get(name);
	        if (s == null) s = "";
	        
	        if (regexp != null) {
	        	s = transform(s);
	        }
	        
	        return s;
        }

		private String transform(String s) {
			PatternFactory pf = new JoniPatternFactory();
			kkckkc.syntaxpane.regex.Pattern p = pf.create(regexp);
			return p.matcher(s).replaceAll(format);
        }
	}
}
