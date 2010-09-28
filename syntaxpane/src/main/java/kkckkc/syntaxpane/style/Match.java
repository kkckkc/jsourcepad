package kkckkc.syntaxpane.style;


public class Match implements Comparable<Match> {
	public static Match NO_MATCH = new Match(false, null);
	public static Match MATCH = new Match(true, null);
	
	private boolean match;

	private int[] score;
	
	public Match(boolean match, int[] score) {
		this.match = match;
		this.score = score;
	}

	public boolean isMatch() {
		return match;
	}
	
	public String toString() {
		return "" + match;
	}

	public int compareTo(Match o) {
        if (o == this) return 0;
        
		if (! o.isMatch()) return 1;
		if (! isMatch()) return -1;
		
		if (score == null) return -1;
		if (o.score == null) return 1;
		
		int i;
		for (i = 0; i < score.length; i++) {
			if (i >= o.score.length) return 1;
			
			if (score[i] > o.score[i]) return 1;
			if (score[i] < o.score[i]) return -1;
		}
		
		if (i < o.score.length) return -1;
		
		return 0;
	}

	public int[] getScore() {
		return score;
	}
}