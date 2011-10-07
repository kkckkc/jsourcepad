package kkckkc.syntaxpane.parse;

public abstract class AbstractCharProvider implements CharProvider {
	private static final int BUFFER_SIZE = 150;
	
	public int find(int start, char c) {
		int o = start;
		int len = getLength();
		while (o != len) {
			CharSequence cs = getSubSequence(o, Math.min(len, o + BUFFER_SIZE));
			int csLength = cs.length();
			
			for (int i = 0; i < csLength; i++) {
				if (cs.charAt(i) == c) {
					return o + i;
				}
			}
			
			o += csLength;
		}
		
		return -1;
	}
}
