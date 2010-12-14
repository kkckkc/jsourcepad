package kkckkc.utils;

public class CharSequenceUtils {
	public static boolean startsWith(CharSequence charSequence, CharSequence potentialPrefix) {
		int len = potentialPrefix.length();
		if (charSequence.length() < len) return false;
		
		for (int i = 0; i < len; i++) {
			if (charSequence.charAt(i) != potentialPrefix.charAt(i) || i >= charSequence.length()) {
				return false;
			}
		}
		return true;
	}

    public static boolean isWhitespace(CharSequence charSequence) {
         if (charSequence.length() == 0) return true;
         for (int i = 0; i < charSequence.length(); i++) {
             char c = charSequence.charAt(i);
             if (! Character.isWhitespace(c)) return false;
         }
         return true;
     }
    
}
