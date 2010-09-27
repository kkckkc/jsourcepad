package kkckkc.syntaxpane.style;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import kkckkc.syntaxpane.model.Scope;
import kkckkc.syntaxpane.util.Pair;

import java.util.*;


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
		
		List<Pair<Match, T>> currentMatches = Lists.newArrayListWithCapacity(items.size());
		
		for (T t : items) {
			ScopeSelector selector = scopeSelectorExtractor.getScopeSelector(t);
			
			Match m;
			if (selector == null) {
				m = Match.MATCH;
			} else {
				m = selector.matches(scope, depth);
			}

			if (! m.isMatch()) continue;

			currentMatches.add(new Pair<Match, T>(m, t));
		}

        Collections.sort(currentMatches, new Comparator<Pair<Match, T>>() {
            public int compare(Pair<Match, T> o1, Pair<Match, T> o2) {
                return o1.getFirst().compareTo(o2.getFirst());
            }
        });

		return Collections2.transform(currentMatches, new Function<Pair<Match, T>, T>() {
            public T apply(Pair<Match, T> pair) {
                return pair.getSecond();
            }
        });
    }
}
