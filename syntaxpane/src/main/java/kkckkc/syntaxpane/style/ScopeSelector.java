package kkckkc.syntaxpane.style;

import kkckkc.syntaxpane.model.Scope;

import java.util.*;



public class ScopeSelector {
	private List<Rule> rules = new ArrayList<Rule>();
	
	public void addRule(Rule rule) {
		this.rules.add(rule);
	}
	
	public Match matches(Scope scope, int depth) {
		Match match = Match.NO_MATCH;
		for (Rule rule : rules) {
			Match m = rule.matches(scope, depth);
			if (m.compareTo(match) > 0) {
				match = m;
			}
		}
		return match;
	}

	public static class Rule {
		private List<String> rule;
		private List<String> negativeRule;
		
		public Rule(List<String> rules, List<String> negativeRule) {
			this.rule = rules;
			Collections.reverse(this.rule);

			this.negativeRule = negativeRule;
			if (this.negativeRule != null) {
				Collections.reverse(this.negativeRule);
			}
		}
		
		public Match matches(Scope scope, int depth) {
			Match positiveMatch = matches(scope, rule, depth);
			
			if (! positiveMatch.isMatch()) {
				return Match.NO_MATCH;
			}

			if (this.negativeRule != null) {
				if (! matches(scope, negativeRule, -1).isMatch()) return positiveMatch;
				else return Match.NO_MATCH;
			} else {
				return positiveMatch;
			}
		}
		
		private Match matches(Scope scope, List<String> r, int depth) {
			int[] score;
			if (depth == -1) score = null;
			else score = new int[r.size()];
			
			int i = 0;
			int j = 0;
			
			Iterator<String> ruleIterator = r.iterator();
			String currentRule = ruleIterator.next();
			while (scope != null) {
				String name = scope.getContext().getName();
				if (name == null) {
					scope = scope.getParent();
					continue;
				}
				
				int length;
				if (currentRule != null && (length = matches(name, currentRule)) >= 0) {
					if (score != null) {
						score[j] = (depth - i) << 24 | length;
						j++;
					}
					currentRule = ruleIterator.hasNext() ? ruleIterator.next() : null;
				}
				
				scope = scope.getParent();
				
				if (currentRule == null && scope == null) {
					return new Match(true, score);
				}
				
				i++;
			}
			
			return Match.NO_MATCH;
		}

		private int matches(String scopeElement, String ruleElement) {
			if (ruleElement == null) return 0;
			if (scopeElement == null) return 0;
			
			if (scopeElement.startsWith(ruleElement)) {
				if (scopeElement.length() == ruleElement.length() || 
					scopeElement.charAt(ruleElement.length()) == '.')
					return ruleElement.length();	
			}
			return -1;
		}

		public static Rule parse(String t) {
			StringTokenizer tok = new StringTokenizer(t, " ");

			List<String> positiveRule = new ArrayList<String>(5);
			List<String> negativeRule = new ArrayList<String>(5);
			
			while (tok.hasMoreTokens()) {
				String i = tok.nextToken();
				if (i.startsWith("-")) {
					negativeRule.add(i.substring(1));
				} else {
					positiveRule.add(i);
				}
			}
			
			return new Rule(positiveRule.size() == 0 ? null: positiveRule, negativeRule.size() == 0 ? null : negativeRule);
		}

		
		public String toString() {
			StringBuilder b = new StringBuilder();
			
			List<String> l = new ArrayList<String>();
			l.addAll(rule);
			Collections.reverse(l);
			
			for (String s : l) {
				b.append(s).append(" ");
			}
			
			if (negativeRule != null) {
				l.clear();
				l.addAll(negativeRule);
				Collections.reverse(l);

				b.append("-");
				
				for (String s : l) {
					b.append(s).append(" ");
				}
			}
			
			return b.toString();
		}
	}
	
	public static ScopeSelector parse(String s) {
		ScopeSelector selector = new ScopeSelector();
		
		StringTokenizer tok = new StringTokenizer(s, ",");
		while (tok.hasMoreTokens()) {
			String t = tok.nextToken();
			selector.addRule(Rule.parse(t));
		}
		
		return selector;
	}

	
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (Rule r : rules) {
			b.append(r.toString()).append(", ");
		}
		b.setLength(b.length() - 2);
		return b.toString();
	}
}
