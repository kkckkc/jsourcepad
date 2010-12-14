package kkckkc.syntaxpane.style;

import kkckkc.syntaxpane.model.Scope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


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
		
		public String toString() {
			StringBuilder builder = new StringBuilder();
			
			List<String> list = new ArrayList<String>();
			list.addAll(rule);
			Collections.reverse(list);
			
			for (String s : list) {
				builder.append(s).append(" ");
			}
			
			if (negativeRule != null) {
				list.clear();
				list.addAll(negativeRule);
				Collections.reverse(list);

				builder.append("-");
				
				for (String s : list) {
					builder.append(s).append(" ");
				}
			}
			
			return builder.toString();
		}
	}
	


	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Rule r : rules) {
			builder.append(r.toString()).append(", ");
		}
		builder.setLength(builder.length() - 2);
		return builder.toString();
	}
}
