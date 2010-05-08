package kkckkc.syntaxpane.parse;

public interface CharProvider {
	public static final int NOT_FOUND = -1;
	
	public int getLength();
	public CharSequence getSubSequence(int start, int end);
	public int find(int start, char c);
	
	public static class StringBuffer extends AbstractCharProvider {
		private java.lang.StringBuffer s;
		
		public StringBuffer(java.lang.StringBuffer s) {
			this.s = s;
		}

		public StringBuffer(String input) {
			this(new java.lang.StringBuffer(input));
		}

		@Override
		public int getLength() {
			return s.length();
		}

		@Override
		public CharSequence getSubSequence(int start, int end) {
			return s.substring(start, end);
		}
	}
}
