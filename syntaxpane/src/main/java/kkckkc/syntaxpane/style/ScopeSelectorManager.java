package kkckkc.syntaxpane.style;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import kkckkc.syntaxpane.model.Scope;



public class ScopeSelectorManager {

	public <T> List<T> getMatches(Scope scope, Map<ScopeSelector, T> selectors) {
		int depth = calcDepth(scope);
		
		Match bestMatch = Match.NO_MATCH;
		List<T> currentMatches = new ArrayList<T>(10);
		
		for (Map.Entry<ScopeSelector, T> entry : selectors.entrySet()) {
			Match m = entry.getKey().matches(scope, depth);

			if (! m.isMatch()) continue;
			
			int compare = m.compareTo(bestMatch);
			if (compare > 0) {
				currentMatches.clear();
				currentMatches.add(entry.getValue());
				bestMatch = m;
			} else if (compare == 0) {
				currentMatches.add(entry.getValue());
			}
		}
		
		return currentMatches;
	}

	public <T> T getMatch(Scope scope, Map<ScopeSelector, T> selectors) {
		List<T> matches = getMatches(scope, selectors);
		if (matches == null || matches.isEmpty()) return null;
		return matches.get(0);
	}

	private int calcDepth(Scope scope) {
		int depth = 0;
		while (scope != null) {
			depth++;
			scope = scope.getParent();
		}
		return depth;
	}

	public interface ScopeSelectorExtractor<T> {
		public ScopeSelector getScopeSelector(T t);
    }

	public <T> Collection<T> getAllMatches(Scope scope, List<T> items, ScopeSelectorExtractor<T> scopeSelectorExtractor) {
		int depth = calcDepth(scope);
		
		TreeMap<Match, T> currentMatches = new TreeMap<Match, T>();
		
		for (T t : items) {
			ScopeSelector selector = scopeSelectorExtractor.getScopeSelector(t);
			
			Match m;
			if (selector == null) {
				m = Match.MATCH;
			} else {
				m = selector.matches(scope, depth);
			}

			if (! m.isMatch()) continue;
			
			currentMatches.put(m, t);
		}
		
		return currentMatches.values();
    }
}
