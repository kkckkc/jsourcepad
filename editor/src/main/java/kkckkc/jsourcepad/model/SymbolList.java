package kkckkc.jsourcepad.model;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import kkckkc.jsourcepad.model.bundle.BundleManager;
import kkckkc.jsourcepad.model.bundle.PrefKeys;
import kkckkc.jsourcepad.util.QueryUtils;
import kkckkc.jsourcepad.util.SedUtils;
import kkckkc.syntaxpane.model.LineManager;
import kkckkc.syntaxpane.model.LineManager.Line;
import kkckkc.syntaxpane.model.Scope;
import kkckkc.utils.Pair;

import java.util.Collections;
import java.util.List;

public class SymbolList {
	private Buffer buffer;

	public SymbolList(Buffer buffer) {
		this.buffer = buffer;
	}
	
	public List<Pair<String, Integer>> getSymbols() {
		List<Pair<String, Integer>> symbols = Lists.newArrayList();

		LineManager lm = buffer.getLineManager();
		Line l = lm.getLineByPosition(0);
		while (l != null) {
			Scope root = l.getScope().getRoot();
			visit(l, root, symbols);
			
			l = lm.getNext(l);
		}	
		
		return symbols;
	}
	

	private void visit(Line l, Scope s, List<Pair<String, Integer>> symbolList) {
		BundleManager bundleManager = Application.get().getBundleManager();
		
		Object o = bundleManager.getPreference(PrefKeys.SYMBOL_SHOW_IN_LIST, s);
		if (s.getRoot() != s && o != null) {
			String symbol = l.getCharSequence().subSequence(s.getStart(), s.getEnd()).toString();
			
			String transformation = (String) bundleManager.getPreference(PrefKeys.SYMBOL_TRANSFORMATION, s);
			if (transformation != null) {
				symbol = SedUtils.applySedExpressions(symbol, transformation);
			}
			symbolList.add(new Pair<String, Integer>(symbol, l.getStart() + s.getStart()));
		} else if (s.hasChildren()) {
        	for (Scope sc : s.getChildren()) {
        		visit(l, sc, symbolList);
        	}
		}
    }

    public List<Pair<String, Integer>> getSymbols(final String query) {
        List<Pair<String, Integer>> all = getSymbols();
        List<Pair<String, Integer>> dest = Lists.newArrayList();

        Predicate<String> predicate = QueryUtils.makePredicate(query);

        for (Pair<String, Integer> p : all) {
            if (predicate.apply(p.getFirst())) dest.add(p);
        }

        Ordering<Pair<String, Integer>> scoringOrdering = Ordering.natural().onResultOf(
                new Function<Pair<String, Integer>, Integer>() {
                    public Integer apply(Pair<String, Integer> p) {
                        int score = 0;

                        // Score by matching characters, matches late in string decreases score
                        score -= QueryUtils.getScorePenalty(p.getFirst(), query);

                        return score;
                    }
                }).reverse();

        Collections.sort(dest, scoringOrdering);

        return dest;
    }
}
